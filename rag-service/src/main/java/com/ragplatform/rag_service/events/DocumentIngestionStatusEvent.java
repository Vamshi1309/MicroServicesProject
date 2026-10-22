package com.ragplatform.rag_service.events;

import java.util.UUID;

public record DocumentIngestionStatusEvent(
        UUID documentId,
        String status,
        String message
) {}
