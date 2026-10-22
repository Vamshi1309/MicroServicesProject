
package com.ragplatform.rag_service.events;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ragplatform.rag_service.service.DocumentIngestionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentEventConsumer {

        private static final Logger log = LoggerFactory.getLogger(DocumentEventConsumer.class);

        private final DocumentIngestionService documentIngestionService;
        private final RestTemplate restTemplate;
        private final IngestionStatusProducer statusProducer;

        @Value("${app.document-service.url}")
        private String documentServiceUrl;

        @KafkaListener(topics = "${app.kafka.topics.document-uploaded:document-uploaded}", groupId = "rag-document-group")
        public void consumerDocumentUploaded(DocumentUploadedEvent event)
                        throws Exception {

                UUID documentId = event.documentId();

                log.info(
                                "Received document upload event: id={}, filename={}, contentType={}",
                                documentId,
                                event.originalFileName(),
                                event.contentType());

                try {
                        // 1. Notify document-service that ingestion has started.
                        statusProducer.publishStatus(
                                        documentId,
                                        "PROCESSING",
                                        "Document ingestion started");

                        // 2. Download the original PDF.
                        byte[] pdfBytes = downloadPdf(documentId);

                        // 3. Run the existing ingestion pipeline.
                        var response = documentIngestionService.ingestPdf(
                                        pdfBytes,
                                        event.originalFileName(),
                                        documentId.toString());

                        // 4. Report success only after ingestion completes.
                        statusProducer.publishStatus(
                                        documentId,
                                        "INGESTED",
                                        "Document ingested successfully");

                        log.info(
                                        "Document ingestion completed: id={}, chunks={}, status={}",
                                        response.documentId(),
                                        response.chunkCount(),
                                        response.status());

                } catch (Exception ex) {
                        log.error(
                                        "Document ingestion failed: id={}",
                                        documentId,
                                        ex);

                        // Report failure, then let Kafka's configured error handler
                        // decide whether to retry or send the event to a DLT.
                        try {
                                statusProducer.publishStatus(
                                                documentId,
                                                "FAILED",
                                                "Document ingestion failed");
                        } catch (Exception statusException) {
                                log.error(
                                                "Failed to publish ingestion failure status: id={}",
                                                documentId,
                                                statusException);
                                ex.addSuppressed(statusException);
                        }

                        throw ex;
                }
        }

        private byte[] downloadPdf(UUID documentId) throws IOException {

                String url = documentServiceUrl
                                + "/documents/{documentId}/file";

                ResponseEntity<byte[]> response = restTemplate.getForEntity(
                                url,
                                byte[].class,
                                documentId);

                byte[] pdfBytes = response.getBody();

                if (pdfBytes == null || pdfBytes.length == 0) {
                        throw new IOException(
                                        "Downloaded PDF is empty for document: " + documentId);
                }

                log.info(
                                "Downloaded PDF: documentId={}, size={} bytes",
                                documentId,
                                pdfBytes.length);

                return pdfBytes;
        }
}
