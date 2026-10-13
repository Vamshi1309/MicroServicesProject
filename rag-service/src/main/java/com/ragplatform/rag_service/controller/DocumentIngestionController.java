package com.ragplatform.rag_service.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.rag_service.service.DocumentIngestionService;
import com.ragplatform.rag_service.service.PdfTextExtractionService;

import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/documents")
@RequiredArgsConstructor
public class DocumentIngestionController {
    private final PdfTextExtractionService pdfTextExtractionService;
    private final DocumentIngestionService documentIngestionService;
    

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> extractPdftext(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded PDF is empty");
        }

        String extractedText = pdfTextExtractionService.extractText(file);

        return Map.of(
                "fileName", file.getOriginalFilename() == null
                        ? "document.pdf"
                        : file.getOriginalFilename(),
                "characterCount", extractedText.length(),
                "text", extractedText);
    }

    @PostMapping(value = "/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> ingestPdf(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded PDF is empty");
        }

        return documentIngestionService.ingestPdf(file);
    }
}
