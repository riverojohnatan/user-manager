package com.example.usermanager.service.impl;

import com.example.usermanager.dto.SignUpRequestDTO;
import com.example.usermanager.dto.PhoneDTO;
import com.example.usermanager.dto.UserResponseDTO;
import com.example.usermanager.entity.Phone;
import com.example.usermanager.entity.User;
import com.example.usermanager.exceptions.LoginException;
import com.example.usermanager.repository.UserRepository;
import com.example.usermanager.service.UserService;
import com.example.usermanager.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=(?:.*[A-Z]){1})(?=(?:.*\\d){2})(?=.*[a-z])[A-Za-z\\d]{8,12}$");

    @Autowired
    private UserRepository userRepository;

    //@Autowired
    //private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;


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

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        //user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(request.getPassword()); // We mustn't save plain password in DB, but for this example we should
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);

        if (request.getPhones() != null) {
            List<Phone> phones = request.getPhones().stream().map(dto -> {
                Phone phone = new Phone();
                phone.setNumber(dto.getNumber());
                phone.setCityCode(dto.getCityCode());
                phone.setCountryCode(dto.getCountryCode());
                phone.setUser(user);
                return phone;
            }).collect(Collectors.toList());
            user.setPhones(phones);
        }

        user.setToken(jwtUtil.generateToken(request.getEmail()));

        User saved = userRepository.save(user);

        return toResponseDTO(saved);
    }

    @Override
    public UserResponseDTO loginUser(String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        Optional<User> optionalUser = userRepository.findByToken(token);

        if (optionalUser.isEmpty()) {
            throw new LoginException("Token inválido o expirado");
        }

        User user = optionalUser.get();
        user.setLastLogin(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setCreated(user.getCreated());
        response.setLastLogin(user.getLastLogin());
        response.setActive(user.isActive());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setToken(user.getToken());

        if (user.getPhones() != null) {
            List<PhoneDTO> phoneDTOs = user.getPhones().stream().map(phone -> {
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
}
