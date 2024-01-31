package org.example.spring_security.dto.request;

import lombok.Builder;

@Builder
public record UserLoginRequestDto(
        String email,
        String password
) {
}
