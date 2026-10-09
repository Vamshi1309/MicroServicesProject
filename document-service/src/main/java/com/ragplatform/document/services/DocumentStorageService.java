package com.ragplatform.document.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentStorageService {

    @Value("${app.storage.location:./uploads}")
    private String storageLocation;

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalFileName = StringUtils
                .cleanPath(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());

        String storedFileName = UUID.randomUUID() + "_" + Paths.get(originalFileName).getFileName();

        Path storageDirectory = Paths.get(storageLocation).toAbsolutePath().normalize();

        Files.createDirectories(storageDirectory);

        Path destination = storageDirectory.resolve(storedFileName).normalize();

        if (!destination.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(file.getInputStream(), destination);
        return destination.toString();
    }
}
