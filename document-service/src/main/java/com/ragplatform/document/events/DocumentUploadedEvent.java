package com.ragplatform.document.events;

import java.util.UUID;

public record DocumentUploadedEvent(
        UUID documentId,
        String originalFileName,
        String contentType) {

}