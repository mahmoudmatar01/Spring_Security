package org.example.spring_security.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.UserLoginRequestDto;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value= "/user/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerUser(@ModelAttribute UserRegistrationRequestDto registerRequest) {

        var registeredUser = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(registeredUser);
    }

    @PostMapping(value= "/admin/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerAdmin(@ModelAttribute UserRegistrationRequestDto registerRequest) {

        var admin = authService.registerAdmin(registerRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(admin);
    }

    @PostMapping(value = "/login", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> loginUser( @ModelAttribute UserLoginRequestDto loginRequest) {

        String authToken = authService.loginUser(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(authToken);
    }

}

