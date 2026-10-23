package com.ragplatform.document.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ragplatform.document.entity.Document;
import com.ragplatform.document.repository.DocumentRepository;
import com.ragplatform.document.services.IngestionSseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class IngestionSseController {

    private final IngestionSseService sseService;

    @GetMapping("/{documentId}/events")
    public SseEmitter subscribe(
            @PathVariable UUID documentId) {
        return sseService.subscribe(documentId);
    }
}
