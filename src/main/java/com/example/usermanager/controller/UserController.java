package com.example.usermanager.controller;

import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.UserResponseDTO;
import com.example.usermanager.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "User endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody SignUpRequestDTO request) {
        UserResponseDTO response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestHeader("Authorization") String token) {
        UserResponseDTO response = userService.loginUser(token);
        return ResponseEntity.ok(response);
    }
}
