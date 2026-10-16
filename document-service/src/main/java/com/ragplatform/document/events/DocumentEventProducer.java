package com.ragplatform.document.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventProducer {
    private final KafkaTemplate<String, DocumentUploadedEvent> kafkaTemplate;

    @Value("${app.kafka.topics.document-uploaded:document-uploaded}")
    private String topicName;

    public DocumentEventProducer(KafkaTemplate<String, DocumentUploadedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        kafkaTemplate.send(
                topicName,
                event.documentId().toString(),
                event);
    }
}
