package com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto;

import java.util.UUID;

import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryForUserRequest(
        @NotBlank @CharactersOnly @Size(min = 5) String name,
        @NotBlank @CharactersOnly String type,
        @NotBlank UUID parentCategoryId,
        @AssertTrue boolean active) {

}
