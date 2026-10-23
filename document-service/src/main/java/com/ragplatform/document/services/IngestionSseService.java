package com.ragplatform.document.services;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ragplatform.document.entity.Document;
import com.ragplatform.document.events.DocumentIngestionStatus;
import com.ragplatform.document.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngestionSseService {
    private final DocumentRepository documentRepository;

    private final ConcurrentHashMap<UUID, Set<SseEmitter>> clients = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID documentId) {
        SseEmitter emitter = new SseEmitter(0L);

        clients.computeIfAbsent(
                documentId,
                id -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> remove(documentId, emitter));
        emitter.onTimeout(() -> remove(documentId, emitter));
        emitter.onError(error -> remove(documentId, emitter));

        Document document = documentRepository.findById(documentId)
                .orElse(null);

        if (document == null) {
            emitter.completeWithError(
                    new IllegalArgumentException("Document not found: " + documentId));
            return emitter;
        }

        DocumentIngestionStatus status = document.getIngestionStatus();

        if (status != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("ingestion-status")
                                .data(status.name()));

                if (status == DocumentIngestionStatus.INGESTED
                        || status == DocumentIngestionStatus.FAILED) {
                    emitter.complete();
                    remove(documentId, emitter);
                }
            } catch (IOException exception) {
                emitter.completeWithError(exception);
                remove(documentId, emitter);
            }
        }

        return emitter;
    }

    public void publish(UUID documentId, DocumentIngestionStatus status) {
        Set<SseEmitter> emitters = clients.get(documentId);

        if (emitters == null) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("ingestion-status")
                                .data(status.name()));

                if (status == DocumentIngestionStatus.INGESTED
                        || status == DocumentIngestionStatus.FAILED) {
                    emitter.complete();
                    remove(documentId, emitter);
                }

            } catch (IOException exception) {
                emitter.completeWithError(exception);
                remove(documentId, emitter);
            }
        }
    }

    private void remove(UUID documentId, SseEmitter emitter) {
        Set<SseEmitter> emitters = clients.get(documentId);

        if (emitters != null) {
            emitters.remove(emitter);

            if (emitters.isEmpty()) {
                clients.remove(documentId, emitters);
            }
        }
    }
}
