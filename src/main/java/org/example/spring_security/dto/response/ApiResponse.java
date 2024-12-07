package org.example.spring_security.dto.response;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
         int statusCode,
         boolean isSuccess,
         T data,
         String message
) {
}
