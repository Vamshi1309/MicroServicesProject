package com.ragplatform.document.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ragplatform.document.entity.Document;
import com.ragplatform.document.repository.DocumentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentIngestionStatusConsumer {
    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionStatusConsumer.class);

    private final DocumentRepository documentRepository;

    @KafkaListener(topics = "${app.kafka.topics.document-ingestion-status:document-ingestion-status}", groupId = "document-status-group")
    @Transactional
    public void consumeStatus(DocumentIngestionStatusEvent event) {
        log.info(
                "Received ingestion status: documentId={}, status={}",
                event.documentId(),
                event.status());

        final DocumentIngestionStatus status;

        try {
            status = DocumentIngestionStatus.valueOf(event.status());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Invalid ingestion status: " + event.status(), ex);
        }

        Document document = documentRepository.findById(event.documentId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + event.documentId()));

        document.setIngestionStatus(status);
        documentRepository.save(document);

        log.info(
                "Document status updated: documentId={}, status={}",
                document.getId(),
                document.getIngestionStatus());
    }
}
