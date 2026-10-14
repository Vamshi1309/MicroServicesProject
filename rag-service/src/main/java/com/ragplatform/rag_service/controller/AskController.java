package com.ragplatform.rag_service.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ragplatform.rag_service.service.RagQuestionAnswerService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {
    private final RagQuestionAnswerService ragQuestionAnswerService;

    @PostMapping("/{docId}")
    public Map<String, String> askQuestion(
            @PathVariable String docId,
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        String answer = ragQuestionAnswerService.answerQuestion(
                docId, question);

        return Map.of("answer", answer);
    }
}
