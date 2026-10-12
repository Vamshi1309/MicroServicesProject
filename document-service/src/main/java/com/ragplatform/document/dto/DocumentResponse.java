package com.ragplatform.document.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private UUID id;
    private String originalFileName;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}