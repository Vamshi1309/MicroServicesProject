package com.ragplatform.document.services;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ragplatform.document.dto.DocumentResponse;
import com.ragplatform.document.entity.Document;
import com.ragplatform.document.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

        private final DocumentStorageService storageService;
        private final DocumentRepository documentRepository;
        private final RagServiceClient ragServiceClient;

        public DocumentResponse uploadDocument(MultipartFile file) throws IOException {

                // 1. Store the actual file on disk
                String storagePath = storageService.storeFile(file);

                // 2. Create the database entity
                Document document = new Document();

                document.setOriginalFileName(file.getOriginalFilename());
                document.setStoredFileName(
                                Paths.get(storagePath).getFileName().toString());
                document.setContentType(
                                file.getContentType() != null
                                                ? file.getContentType()
                                                : "application/octet-stream");
                document.setFileSize(file.getSize());
                document.setStoragePath(storagePath);

                // 3. Save metadata in PostgreSQL
                Document savedDocument = documentRepository.save(document);
                ragServiceClient.ingestDocument(file);

                // 4. Convert the entity into a safe API response
                return new DocumentResponse(
                                savedDocument.getId(),
                                savedDocument.getOriginalFileName(),
                                savedDocument.getContentType(),
                                savedDocument.getFileSize(),
                                savedDocument.getUploadedAt());
        }
}
