package org.example.spring_security.dto.response;

import lombok.Builder;

@Builder
public record UserRegisterResponseDto(
        String uuid,
        String firstName,
        String lastName,
        String userEmail
) {
}
