package com.example.usermanager.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PhoneDTO {

    @NotNull(message = "El número es obligatorio")
    private Long number;

    @NotNull(message = "El código de ciudad es obligatorio")
    private Integer cityCode;

    @NotBlank(message = "El código de país es obligatorio")
    private String countryCode;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
