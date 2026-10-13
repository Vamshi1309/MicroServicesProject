package com.ragplatform.document.dto;

public record RagIngestionResponse(
        String documentId,
        String fileName,
        int characterCount,
        int chunkCount,
        String status) {
}
