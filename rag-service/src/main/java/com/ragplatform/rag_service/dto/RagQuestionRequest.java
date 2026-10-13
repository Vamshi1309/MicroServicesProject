package com.ragplatform.rag_service.dto;

public record RagQuestionRequest(
        String documentId,
        String question) {
}
