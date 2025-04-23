package com.example.usermanager.controller.advice;

import com.example.usermanager.dto.errors.ErrorResponseDTO;
import com.example.usermanager.exceptions.DuplicateUserException;
import com.example.usermanager.exceptions.LoginException;
import com.example.usermanager.exceptions.WrongEmailException;
import com.example.usermanager.exceptions.WrongPasswordException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    @ApiResponse(description = "Structure of error", responseCode = "500|400|404|409")
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO("Application throw an unknown error",
                "UNKNOWN_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ApiResponse(description = "Wrong argument", responseCode = "400")
    public ResponseEntity<ErrorResponseDTO> handleException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorResponseDTO("Some arguments are wrong or missing",
                "INVALID_ARGUMENT", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
        StringBuffer errors = new StringBuffer();
        e.getBindingResult().getFieldErrors().forEach(err -> {
            errors.append(err.getField()).append(": ").append(err.getDefaultMessage());
        });
        return new ResponseEntity<>(new ErrorResponseDTO(errors.toString(), "INVALID_ARGUMENT",
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = LoginException.class)
    @ApiResponse(description = "Wrong login", responseCode = "401")
    public ResponseEntity<ErrorResponseDTO> handleException(LoginException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage(), "WRONG_LOGIN",
                HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = DuplicateUserException.class)
    @ApiResponse(description = "Wrong login", responseCode = "401")
    public ResponseEntity<ErrorResponseDTO> handleException(DuplicateUserException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage(), "DUPLICATE_USER",
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = WrongEmailException.class)
    @ApiResponse(description = "Wrong login", responseCode = "401")
    public ResponseEntity<ErrorResponseDTO> handleException(WrongEmailException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage(), "WRONG_EMAIL_FORMAT",
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = WrongPasswordException.class)
    @ApiResponse(description = "Wrong login", responseCode = "401")
    public ResponseEntity<ErrorResponseDTO> handleException(WrongPasswordException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage(), "WRONG_PASSWORD_FORMAT",
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
