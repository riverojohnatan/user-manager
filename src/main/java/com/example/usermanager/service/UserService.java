package com.example.usermanager.service;

import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(SignUpRequestDTO request);
    UserResponseDTO loginUser(String token);
}
