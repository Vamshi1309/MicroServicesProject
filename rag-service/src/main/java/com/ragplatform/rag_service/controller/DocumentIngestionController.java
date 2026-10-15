package com.ragplatform.rag_service.controller;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.rag_service.service.DocumentIngestionService;
import com.ragplatform.rag_service.service.PdfTextExtractionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

import com.ragplatform.rag_service.dto.response.DocumentExtractionResponse;
import com.ragplatform.rag_service.dto.response.DocumentIngestionResponse;
import com.ragplatform.rag_service.dto.response.ApiResponse;

@RestController
@RequestMapping("/internal/documents")
@RequiredArgsConstructor
public class DocumentIngestionController {
    private final PdfTextExtractionService pdfTextExtractionService;
    private final DocumentIngestionService documentIngestionService;

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentExtractionResponse>> extractPdfText(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded PDF is empty");
        }

        String extractedText = pdfTextExtractionService.extractText(file);

        DocumentExtractionResponse response = new DocumentExtractionResponse(
                file.getOriginalFilename() == null
                        ? "document.pdf"
                        : file.getOriginalFilename(),
                extractedText.length(),
                extractedText);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "\"PDF text extracted successfully\"", response));

    }

    @PostMapping(value = "/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentIngestionResponse>> ingestPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentId") String documentId) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded PDF is empty");
        }

        DocumentIngestionResponse response = documentIngestionService.ingestPdf(file, documentId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Document ingested successfully",
                        response));
    }
}
