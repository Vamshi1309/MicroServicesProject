
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

    @Value("${app.document-service.url}")
    private String documentServiceUrl;

    @KafkaListener(topics = "${app.kafka.topics.document-uploaded:document-uploaded}")
    public void consumerDocumentUploaded(DocumentUploadedEvent event)
            throws IOException {

        log.info(
                "Received document upload event: id={}, filename={}, contentType={}",
                event.documentId(),
                event.originalFileName(),
                event.contentType());

        byte[] pdfBytes = downloadPdf(event.documentId());

        var response = documentIngestionService.ingestPdf(
                pdfBytes,
                event.originalFileName(),
                event.documentId().toString());

        log.info(
                "Document ingestion completed: id={}, chunks={}, status={}",
                response.documentId(),
                response.chunkCount(),
                response.status());
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
