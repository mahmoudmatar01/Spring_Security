package org.example.spring_security.mapper;

import org.example.spring_security.dto.response.UserRegisterResponseDto;
import org.example.spring_security.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public class UserToUserResponseDtoMapper implements Function<User, UserRegisterResponseDto> {
    @Override
    public UserRegisterResponseDto apply(User user) {
        return UserRegisterResponseDto.builder()
                .uuid(user.getUuidCode().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userEmail(user.getEmail())
                .build();
    }
}
