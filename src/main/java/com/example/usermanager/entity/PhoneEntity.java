package com.example.usermanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "phones")
@Data
public class PhoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;
    private Integer cityCode;
    private String countryCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
