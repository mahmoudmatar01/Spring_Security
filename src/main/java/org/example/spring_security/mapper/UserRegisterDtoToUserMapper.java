package org.example.spring_security.mapper;

import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.entity.User;
import org.example.spring_security.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class UserRegisterDtoToUserMapper implements Function<UserRegistrationRequestDto, User> {
    private final PasswordEncoder passwordEncoder;
    @Override
    public User apply(UserRegistrationRequestDto userRegistrationDto) {
        return apply(userRegistrationDto, Role.USER);
    }

    public User apply(UserRegistrationRequestDto adminRequestDto, Role role) {
        return User.builder()
                .uuidCode(UUID.randomUUID())
                .email(adminRequestDto.email())
                .firstName(adminRequestDto.firstName())
                .lastName(adminRequestDto.lastName())
                .role(role)
                .password(passwordEncoder.encode(adminRequestDto.password()))
                .build();
    }
}
