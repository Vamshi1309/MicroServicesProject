package ccom.ragplatform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(
            int statusCode,
            String message,
            T data) {

        return new ApiResponse<>(
                statusCode,
                message,
                data);
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data) {

        return new ApiResponse<>(
                200,
                message,
                data);
    }

    public static ApiResponse<Void> success(String message) {

        return new ApiResponse<>(
                200,
                message,
                null);
    }

    public static ApiResponse<Void> error(
            int statusCode,
            String message) {

        return new ApiResponse<>(
                statusCode,
                message,
                null);
    }

}
