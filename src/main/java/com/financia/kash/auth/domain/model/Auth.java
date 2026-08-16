package com.financia.kash.auth.domain.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Auth {
    @NotBlank
    @Length(min = 3)
    private final String name;
    @NotBlank
    @Length(min = 3)
    private final String lastName;
    @NotBlank
    @Email(message = "El correo tiene que tener un formato adecuado")
    private final String email;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    private final String password;
    @NotBlank
    private final String role;

}
