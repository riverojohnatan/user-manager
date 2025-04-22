package com.example.usermanager.dto.errors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDTO {
    private String message;
    private String code;
    private int status;
}
