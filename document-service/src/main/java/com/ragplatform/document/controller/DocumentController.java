package com.ragplatform.document.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.document.services.DocumentService;
import com.ragplatform.document.dto.DocumentResponse;
import com.ragplatform.document.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(@RequestPart("file") MultipartFile file)
            throws IOException {
        DocumentResponse documentResponse = documentService.uploadDocument(file);
        ApiResponse<DocumentResponse> response = ApiResponse.success(HttpStatus.CREATED.value(),
                "Document uploaded successfully", documentResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
