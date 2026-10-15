package com.ragplatform.rag_service.dto.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int statusCode,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(
            HttpStatus status,
            String message,
            T data) {

        return new ApiResponse<>(
                status.value(),
                message,
                data
        );
    }

    public static <T> ApiResponse<T> error(
            HttpStatus status,
            String message) {

        return new ApiResponse<>(
                status.value(),
                message,
                null
        );
    }
}
