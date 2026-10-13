package com.ragplatform.rag_service.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentRetrievalService {

    private final VectorStore vectorStore;

    public List<Document> retrieveChunks(
            String documentId,
            String question) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(4)
                        .filterExpression(
                                "documentId == '" + documentId + "'")
                        .build());
    }
}