package com.financia.kash.auth.infrastructure.adapter.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record Confirm2faRequest(
        @NotBlank(message = "El código no puede estar vacío")
        @Pattern(regexp = "^\\d{6}$", message = "El código debe tener 6 dígitos")
        String code) {

}
