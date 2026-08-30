package com.financia.kash.shared.infrastructure.utils.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.financia.kash.shared.infrastructure.utils.validation.imp.CharactersOnlyValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CharactersOnlyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CharactersOnly {

    String message() default "Solo se eaceptan caracteres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
