
package com.ragplatform.rag_service.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.rag_service.dto.response.ApiResponse;
import com.ragplatform.rag_service.dto.response.DocumentExtractionResponse;
import com.ragplatform.rag_service.dto.response.DocumentIngestionResponse;
import com.ragplatform.rag_service.exception.InvalidDocumentException;
import com.ragplatform.rag_service.service.DocumentIngestionService;
import com.ragplatform.rag_service.service.PdfTextExtractionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/documents")
@RequiredArgsConstructor
public class DocumentIngestionController {

    private final PdfTextExtractionService pdfTextExtractionService;
    private final DocumentIngestionService documentIngestionService;

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentExtractionResponse>> extractPdfText(
            @RequestParam("file") MultipartFile file) throws IOException {

        validateFile(file);

        String extractedText = pdfTextExtractionService.extractText(file);

        if (extractedText == null || extractedText.isBlank()) {
            throw new InvalidDocumentException(
                    "No readable text found in the uploaded PDF.");
        }

        DocumentExtractionResponse response = new DocumentExtractionResponse(
                file.getOriginalFilename() == null
                        ? "document.pdf"
                        : file.getOriginalFilename(),
                extractedText.length(),
                extractedText);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "PDF text extracted successfully",
                        response));
    }

    @PostMapping(value = "/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentIngestionResponse>> ingestPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentId") String documentId) throws IOException {

        validateFile(file);

        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Document ID must not be blank.");
        }

        DocumentIngestionResponse response = documentIngestionService.ingestPdf(file, documentId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Document ingested successfully",
                        response));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDocumentException(
                    "Uploaded PDF file must not be empty.");
        }

        String filename = file.getOriginalFilename();

        if (filename == null
                || !filename.toLowerCase().endsWith(".pdf")) {
            throw new InvalidDocumentException(
                    "Only PDF files are supported.");
        }
    }
}
