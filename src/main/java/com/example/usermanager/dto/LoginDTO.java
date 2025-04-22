package com.example.usermanager.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginDTO {
    private String name;
    private String email;
    private String password;
    private List<PhoneDTO> phones;
}
