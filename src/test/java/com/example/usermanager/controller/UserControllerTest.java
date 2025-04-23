package com.example.usermanager.controller;

import com.example.usermanager.dto.PhoneDTO;
import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.UserResponseDTO;
import com.example.usermanager.exceptions.DuplicateUserException;
import com.example.usermanager.exceptions.LoginException;
import com.example.usermanager.exceptions.WrongEmailException;
import com.example.usermanager.exceptions.WrongPasswordException;
import com.example.usermanager.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setCountryCode("AR");
        phoneDTO.setCityCode(1);
        phoneDTO.setNumber(1L);
        List<PhoneDTO> phoneDTOList = Lists.newArrayList(phoneDTO);
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password12");
        request.setPhones(phoneDTOList);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setName(request.getName());
        responseDTO.setEmail(request.getEmail());
        responseDTO.setCreated(LocalDateTime.now());
        responseDTO.setLastLogin(LocalDateTime.now());
        responseDTO.setToken("sample.token.value");
        responseDTO.setActive(true);
        responseDTO.setPassword("ENCRYPTED");
        responseDTO.setPhones(phoneDTOList);

        Mockito.when(userService.registerUser(any(SignUpRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.registerUser(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Test User");
        request.setEmail("invalid-email");
        request.setPassword("Password12");

        Mockito.when(userService.registerUser(any(SignUpRequestDTO.class)))
                .thenThrow(new WrongEmailException("Formato de email inválido"));

        WrongEmailException exception = assertThrows(
                WrongEmailException.class,
                () -> userController.registerUser(request)
        );

        assertEquals("Formato de email inválido", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("weak");

        Mockito.when(userService.registerUser(any(SignUpRequestDTO.class)))
                .thenThrow(new WrongPasswordException("Formato de contraseña inválido"));

        WrongPasswordException exception = assertThrows(
                WrongPasswordException.class,
                () -> userController.registerUser(request)
        );

        assertEquals("Formato de contraseña inválido", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("Existing User");
        request.setEmail("existing@example.com");
        request.setPassword("Password12");

        Mockito.when(userService.registerUser(any(SignUpRequestDTO.class)))
                .thenThrow(new DuplicateUserException("El usuario ya existe"));

        DuplicateUserException exception = assertThrows(
                DuplicateUserException.class,
                () -> userController.registerUser(request)
        );

        assertEquals("El usuario ya existe", exception.getMessage());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        String token = "Bearer sample.valid.token";

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setName("Test User");
        responseDTO.setEmail("test@example.com");
        responseDTO.setCreated(LocalDateTime.now());
        responseDTO.setLastLogin(LocalDateTime.now());
        responseDTO.setToken("sample.valid.token");
        responseDTO.setActive(true);
        responseDTO.setPhones(Collections.emptyList());

        Mockito.when(userService.loginUser(token)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.loginUser(token);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Test User", response.getBody().getName());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        String token = "Bearer invalid.token";

        Mockito.when(userService.loginUser(token))
                .thenThrow(new LoginException("Token inválido o expirado"));

        LoginException exception = assertThrows(
                LoginException.class,
                () -> userController.loginUser(token)
        );

        assertEquals("Token inválido o expirado", exception.getMessage());
    }
}