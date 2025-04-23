package com.example.usermanager.service.impl;

import com.example.usermanager.dto.PhoneDTO;
import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.UserResponseDTO;
import com.example.usermanager.entity.User;
import com.example.usermanager.exceptions.DuplicateUserException;
import com.example.usermanager.exceptions.LoginException;
import com.example.usermanager.exceptions.WrongEmailException;
import com.example.usermanager.exceptions.WrongPasswordException;
import com.example.usermanager.repository.UserRepository;
import com.example.usermanager.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldThrowWrongEmailException_whenEmailIsInvalid() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("John Doe");
        request.setEmail("invalidemail.com");
        request.setPassword("Password123");

        WrongEmailException exception = assertThrows(WrongEmailException.class, () -> userServiceImpl.registerUser(request));
        assertEquals("Formato de email inválido", exception.getMessage());
    }

    @Test
    void registerUser_shouldThrowWrongPasswordException_whenPasswordIsInvalid() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("John Doe");
        request.setEmail("user@example.com");
        request.setPassword("short1");

        WrongPasswordException exception = assertThrows(WrongPasswordException.class, () -> userServiceImpl.registerUser(request));
        assertEquals("Formato de contraseña inválido", exception.getMessage());
    }

    @Test
    void registerUser_shouldThrowDuplicateUserException_whenUserAlreadyExists() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("John Doe");
        request.setEmail("user@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> userServiceImpl.registerUser(request));
        assertEquals("El usuario ya existe", exception.getMessage());
    }

    @Test
    void registerUser_shouldRegisterUserSuccessfully() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("John Doe");
        request.setEmail("user@example.com");
        request.setPassword("Password123");
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber(1L);
        phoneDTO.setCityCode(1);
        phoneDTO.setCountryCode("54");
        request.setPhones(Collections.singletonList(phoneDTO));

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("generatedToken");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userServiceImpl.registerUser(request);

        assertNotNull(response.getId());
        assertNotNull(response.getCreated());
        assertNotNull(response.getLastLogin());
        assertTrue(response.isActive());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals("encodedPassword", response.getPassword());
        assertEquals("generatedToken", response.getToken());
        assertNotNull(response.getPhones());
        assertEquals(1, response.getPhones().size());
        assertEquals(phoneDTO.getNumber(), response.getPhones().get(0).getNumber());
        assertEquals(phoneDTO.getCityCode(), response.getPhones().get(0).getCityCode());
        assertEquals(phoneDTO.getCountryCode(), response.getPhones().get(0).getCountryCode());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(request.getName(), savedUser.getName());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("generatedToken", savedUser.getToken());
        assertNotNull(savedUser.getPhones());
        assertEquals(1, savedUser.getPhones().size());
        assertEquals(phoneDTO.getNumber(), savedUser.getPhones().get(0).getNumber());
        assertEquals(phoneDTO.getCityCode(), savedUser.getPhones().get(0).getCityCode());
        assertEquals(phoneDTO.getCountryCode(), savedUser.getPhones().get(0).getCountryCode());
    }

    @Test
    void registerUser_shouldRegisterUserNoPhoneSuccessfully() {
        SignUpRequestDTO request = new SignUpRequestDTO();
        request.setName("John Doe");
        request.setEmail("user@example.com");
        request.setPassword("Password123");
        request.setPhones(null);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("generatedToken");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userServiceImpl.registerUser(request);

        assertNotNull(response.getId());
        assertNotNull(response.getCreated());
        assertNotNull(response.getLastLogin());
        assertTrue(response.isActive());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals("encodedPassword", response.getPassword());
        assertEquals("generatedToken", response.getToken());
        assertNull(response.getPhones());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(request.getName(), savedUser.getName());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("generatedToken", savedUser.getToken());
        assertNull(savedUser.getPhones());
    }

    @Test
    void loginUser_shouldThrowLoginException_whenTokenIsInvalid() {
        String invalidToken = "Bearer invalidToken";

        when(userRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        LoginException exception = assertThrows(LoginException.class, () -> userServiceImpl.loginUser(invalidToken));

        assertEquals("Token inválido o expirado", exception.getMessage());
    }

    @Test
    void loginUser_shouldUpdateLastLoginAndReturnUser_whenTokenIsValid() {
        String validToken = "Bearer validToken";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("user@example.com");
        user.setToken("validToken");
        user.setLastLogin(LocalDateTime.now().minusDays(1));
        user.setCreated(LocalDateTime.now().minusDays(2));
        user.setActive(true);

        when(userRepository.findByToken("validToken")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userServiceImpl.loginUser(validToken);

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertTrue(response.isActive());
        assertNotNull(response.getLastLogin());
        assertNotNull(response.getCreated());
        assertEquals("validToken", response.getToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertNotNull(updatedUser.getLastLogin());
    }
}