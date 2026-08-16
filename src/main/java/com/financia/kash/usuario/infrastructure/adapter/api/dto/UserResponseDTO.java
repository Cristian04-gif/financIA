package com.financia.kash.usuario.infrastructure.adapter.api.dto;

public record UserResponseDTO(String name,
        String lastName,
        String email,
        String status,
        String role) {

}
