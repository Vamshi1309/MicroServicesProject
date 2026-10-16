package com.ragplatform.rag_service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(DocumentEventConsumer.class);

    @KafkaListener(topics = "${app.kafka.topics.document-uploaded:document-uploaded}")
    public void consumerDocumentUploaded(DocumentUploadedEvent event) {
        log.info(
                "Received document upload event: id={}, filename={}, contentType={}",
                event.documentId(),
                event.originalFileName(),
                event.contentType());
    }
}
