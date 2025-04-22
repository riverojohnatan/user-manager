package com.example.usermanager.service.impl;

import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.PhoneDTO;
import com.example.usermanager.dto.UserResponseDTO;
import com.example.usermanager.entity.PhoneEntity;
import com.example.usermanager.entity.UserEntity;
import com.example.usermanager.repository.UserRepository;
import com.example.usermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=(?:.*[A-Z]){1})(?=(?:.*\\d){2})(?=.*[a-z])[A-Za-z\\d]{8,12}$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public UserResponseDTO registerUser(SignUpRequestDTO request) {
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new IllegalArgumentException("Formato de contraseña inválido");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setToken(UUID.randomUUID().toString()); // JWT se manejará luego
        user.setActive(true);

        if (request.getPhones() != null) {
            List<PhoneEntity> phones = request.getPhones().stream().map(dto -> {
                PhoneEntity phone = new PhoneEntity();
                phone.setNumber(dto.getNumber());
                phone.setCityCode(dto.getCityCode());
                phone.setCountryCode(dto.getCountryCode());
                phone.setUser(user);
                return phone;
            }).collect(Collectors.toList());
            user.setPhones(phones);
        }

        UserEntity saved = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(saved.getId());
        response.setCreated(saved.getCreated());
        response.setLastLogin(saved.getLastLogin());
        response.setToken(saved.getToken());
        response.setActive(saved.isActive());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setPassword(request.getPassword());
        if (saved.getPhones() != null) {
            List<PhoneDTO> phoneDTOs = saved.getPhones().stream().map(phone -> {
                PhoneDTO dto = new PhoneDTO();
                dto.setNumber(phone.getNumber());
                dto.setCityCode(phone.getCityCode());
                dto.setCountryCode(phone.getCountryCode());
                return dto;
            }).collect(Collectors.toList());
            response.setPhones(phoneDTOs);
        }
        return response;
    }

    @Override
    public UserResponseDTO loginUser(String token) {
        // Implementación futura con JWT
        return null;
    }
}
