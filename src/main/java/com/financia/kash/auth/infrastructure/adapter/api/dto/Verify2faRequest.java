package com.financia.kash.auth.infrastructure.adapter.api.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Verify2faRequest {

    @NotBlank
    private String preToken;
    @Length(min = 6, max = 6, message = "El codigo tiene que tener 6 digitos")
    private String code;
}
