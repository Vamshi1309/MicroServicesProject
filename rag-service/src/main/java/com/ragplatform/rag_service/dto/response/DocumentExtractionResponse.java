package com.ragplatform.rag_service.dto.response;

public record DocumentExtractionResponse(
        String fileName,
        int characterCount,
        String text) {
}
