package org.example.spring_security.mapper;

import org.example.spring_security.dto.response.UserRegisterResponseDto;
import org.example.spring_security.entity.UserData;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public class UserToUserResponseDtoMapper implements Function<UserData, UserRegisterResponseDto> {
    @Override
    public UserRegisterResponseDto apply(UserData user) {
        return UserRegisterResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userEmail(user.getEmail())
                .build();
    }
}
