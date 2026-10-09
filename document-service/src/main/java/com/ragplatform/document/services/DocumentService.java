package com.ragplatform.document.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.document.repository.DocumentRepository;
import com.ragplatform.document.entity.Document;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentStorageService storageService;
    private final DocumentRepository documentRepository;

    public Document uploadDocument(MultipartFile file) throws IOException {
        String storagePath = storageService.storeFile(file);
        Document document = new Document();
        document.setOriginalFileName(file.getOriginalFilename());
        document.setStoredFileName(java.nio.file.Paths.get(storagePath).getFileName().toString());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStoragePath(storagePath);
        return documentRepository.save(document);
    }
}
