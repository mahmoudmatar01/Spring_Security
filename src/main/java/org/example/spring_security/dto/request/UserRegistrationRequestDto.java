package org.example.spring_security.dto.request;
import lombok.Builder;

@Builder
public record UserRegistrationRequestDto(
         String firstName,
         String lastName,
         String email,
         String password,
         String confirmPassword

) {
}
