package org.example.spring_security.exception;


import org.example.spring_security.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .isSuccess(false)
                .data(null)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        var response = ApiResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .isSuccess(false)
                .data(null)
                .message("Invalid username or password")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


}