package com.ragplatform.rag_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TextChunkingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    public List<String> splitIntoChunks(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        // Normalize whitespace from OCR output.
        text = text.replaceAll("[\\t\\r]+", " ")
                   .replaceAll(" *\\n *", "\n")
                   .trim();

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            // Prefer ending at a word boundary.
            if (end < text.length()) {
                int boundary = text.lastIndexOf(' ', end);

                if (boundary > start + CHUNK_SIZE / 2) {
                    end = boundary;
                }
            }

            String chunk = text.substring(start, end).trim();

            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end == text.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return chunks;
    }
}
