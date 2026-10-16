package com.ragplatform.rag_service.events;

import java.util.UUID;

public record DocumentUploadedEvent(
        UUID documentId,
        String originalFileName,
        String contentType) {
}
