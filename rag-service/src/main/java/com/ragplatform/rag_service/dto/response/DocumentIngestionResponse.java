package com.ragplatform.rag_service.dto.response;

import lombok.Builder;

@Builder
public record DocumentIngestionResponse(
        String documentId,
        String fileName,
        int characterCount,
        int chunkCount,
        String status) {
}