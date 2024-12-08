package org.example.spring_security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.RefreshTokenRequestDto;
import org.example.spring_security.dto.request.UserLoginRequestDto;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.dto.response.ApiResponse;
import org.example.spring_security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.version}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value= "/user/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequestDto registerRequest) {
        var registeredUser = authService.registerUser(registerRequest);
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(registeredUser)
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value= "/admin/register")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserRegistrationRequestDto registerRequest) {
        var admin = authService.registerAdmin(registerRequest);
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(admin)
                .message("Admin registered successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequestDto loginRequest) {
        var loginResponse = authService.loginUser(loginRequest);
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(loginResponse)
                .message("Login successful")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        var refreshResponse = authService.refreshToken(refreshTokenRequestDto);
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(refreshResponse)
                .message("Token refreshed successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}

