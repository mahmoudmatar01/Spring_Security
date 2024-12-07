package org.example.spring_security.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public ResponseEntity<?> home() {
        return ResponseEntity.status(HttpStatus.OK)
                .body("Welcome to Spring Security, Home Page");
    }
}
