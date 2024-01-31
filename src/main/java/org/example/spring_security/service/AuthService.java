package org.example.spring_security.service;


import org.example.spring_security.dto.request.UserLoginRequestDto;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.dto.response.UserRegisterResponseDto;

public interface AuthService {
    UserRegisterResponseDto registerUser(UserRegistrationRequestDto registerRequest);
    UserRegisterResponseDto registerAdmin(UserRegistrationRequestDto adminDto);
    String loginUser(UserLoginRequestDto loginRequest);
}
