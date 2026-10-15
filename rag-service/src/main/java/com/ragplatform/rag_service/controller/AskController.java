package com.ragplatform.rag_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ragplatform.rag_service.service.RagQuestionAnswerService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import com.ragplatform.rag_service.dto.request.RagQuestionRequest;
import com.ragplatform.rag_service.dto.response.AskQuestionResponse;

import com.ragplatform.rag_service.dto.response.ApiResponse;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {
    private final RagQuestionAnswerService ragQuestionAnswerService;

    @PostMapping("/{docId}")
    public ResponseEntity<ApiResponse<AskQuestionResponse>> askQuestion(
            @PathVariable String docId,
            @Valid @RequestBody RagQuestionRequest request) {

        String answer = ragQuestionAnswerService.answerQuestion(
                docId,
                request.question());

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Answer generated successfully",
                        new AskQuestionResponse(answer)));
    }
}
