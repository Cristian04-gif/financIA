package com.financia.kash.movimiento.categoria.infrastructure.adapter.api.dto;

import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryGlobalRequest(
        @NotBlank @Size(min = 5) @CharactersOnly String name,
        @NotBlank @CharactersOnly String type) {

}
