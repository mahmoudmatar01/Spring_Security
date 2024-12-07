package org.example.spring_security.dto.response;

import lombok.Builder;

@Builder
public record UserLoginResponseDto(
        String access_token,
        String refresh_token
) {
}
