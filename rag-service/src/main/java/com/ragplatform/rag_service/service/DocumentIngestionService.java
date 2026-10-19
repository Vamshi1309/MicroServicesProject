
package com.ragplatform.rag_service.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.rag_service.dto.response.DocumentIngestionResponse;
import com.ragplatform.rag_service.exception.InvalidDocumentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final PdfTextExtractionService pdfTextExtractionService;
    private final TextChunkingService textChunkingService;
    private final VectorStore vectorStore;

    public DocumentIngestionResponse ingestPdf(
            MultipartFile file, String docId) throws IOException {

        String filename = file.getOriginalFilename() == null
                ? "document.pdf"
                : file.getOriginalFilename();

        String extractedText = pdfTextExtractionService.extractText(file);

        return ingestExtractedText(extractedText, filename, docId);
    }

    public DocumentIngestionResponse ingestPdf(
            byte[] pdfBytes, String filename, String docId)
            throws IOException {

        String safeFilename = filename == null || filename.isBlank()
                ? "document.pdf"
                : filename;

        String extractedText = pdfTextExtractionService.extractText(
                new ByteArrayInputStream(pdfBytes));

        return ingestExtractedText(
                extractedText, safeFilename, docId);
    }

    private DocumentIngestionResponse ingestExtractedText(
            String extractedText, String filename, String docId)
            throws IOException {

        if (extractedText == null || extractedText.isBlank()) {
            throw new InvalidDocumentException(
                    "No readable text found in the uploaded PDF.");
        }

        List<String> chunks =
                textChunkingService.splitIntoChunks(extractedText);

        if (chunks.isEmpty()) {
            throw new IOException(
                    "No text chunks were created from the PDF");
        }

        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();

            metadata.put("documentId", docId);
            metadata.put("filename", filename);
            metadata.put("chunkIndex", i);

            documents.add(new Document(chunks.get(i), metadata));
        }

        vectorStore.add(documents);

        return DocumentIngestionResponse.builder()
                .documentId(docId)
                .fileName(filename)
                .characterCount(extractedText.length())
                .chunkCount(chunks.size())
                .status("INGESTED")
                .build();
    }
}
