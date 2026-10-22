package com.ragplatform.rag_service.events;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngestionStatusProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStatus(UUID documentId,
            String status,
            String message) {
        var event = new DocumentIngestionStatusEvent(documentId, status, message);

        kafkaTemplate.send(
                "document-ingestion-status",
                documentId.toString(),
                event);
    }
}