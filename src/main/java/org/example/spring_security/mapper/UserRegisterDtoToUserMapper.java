package org.example.spring_security.mapper;

import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.entity.UserData;
import org.example.spring_security.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class UserRegisterDtoToUserMapper implements Function<UserRegistrationRequestDto, UserData> {
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserData apply(UserRegistrationRequestDto userRegistrationDto) {
        return apply(userRegistrationDto, Role.User_Role);
    }

    public UserData apply(UserRegistrationRequestDto adminRequestDto,Role role) {
        return UserData.builder()
                .email(adminRequestDto.email())
                .firstName(adminRequestDto.firstName())
                .lastName(adminRequestDto.lastName())
                .role(role)
                .password(passwordEncoder.encode(adminRequestDto.password()))
                .build();
    }
}
