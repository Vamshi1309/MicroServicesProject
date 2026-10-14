package com.ragplatform.rag_service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {
    private final PdfTextExtractionService pdfTextExtractionService;
    private final TextChunkingService textChunkingService;
    private final VectorStore vectorStore;

    public Map<String, Object> ingestPdf(
        MultipartFile file, String docId) throws IOException {
        String extractedText = pdfTextExtractionService.extractText(file);

        if (extractedText == null || extractedText.isBlank()) {
            throw new IOException(
                    "No readable text found in the uploaded PDF");
        }

        List<String> chunks = textChunkingService.splitIntoChunks(extractedText);

        if (chunks.isEmpty()) {
            throw new IOException(
                    "No text chunks were created from the PDF");
        }

        List<Document> documents = new ArrayList<>();

        String filename = file.getOriginalFilename() == null
                ? "document.pdf"
                : file.getOriginalFilename();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();

            metadata.put("documentId", docId);
            metadata.put("filename", filename);
            metadata.put("chunkIndex", i);

            Document document = new Document(
                    chunks.get(i),
                    metadata);

            documents.add(document);
        }

        vectorStore.add(documents);

        Map<String, Object> result = new HashMap<>();
        result.put("documentId", docId);
        result.put("fileName", filename);
        result.put("characterCount", extractedText.length());
        result.put("chunkCount", chunks.size());
        result.put("status", "INGESTED");

        return result;
    }
}
