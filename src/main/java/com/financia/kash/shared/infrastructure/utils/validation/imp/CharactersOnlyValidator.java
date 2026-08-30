package com.financia.kash.shared.infrastructure.utils.validation.imp;

import com.financia.kash.shared.infrastructure.utils.validation.CharactersOnly;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CharactersOnlyValidator implements ConstraintValidator<CharactersOnly, String> {

    @Override
    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        if (arg0 == null) {
            return true;
        }
        return arg0.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

}
