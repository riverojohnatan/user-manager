package com.example.usermanager.controller.advice;

import com.example.usermanager.dto.errors.ErrorResponseDTO;
import com.example.usermanager.exceptions.LoginException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    @ApiResponse(description = "Structure of error", responseCode = "500|400|404|409")
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .message("Application throw an unknown error")
                .code("UNKNOWN_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ApiResponse(description = "Wrong argument", responseCode = "400")
    public ResponseEntity<ErrorResponseDTO> handleException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .message("Some arguments are wrong or missing")
                .code("INVALID_ARGUMENT")
                .status(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = LoginException.class)
    @ApiResponse(description = "Wrong login", responseCode = "401")
    public ResponseEntity<ErrorResponseDTO> handleException(LoginException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .message(e.getMessage())
                .code("WRONG_LOGIN")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }
}
