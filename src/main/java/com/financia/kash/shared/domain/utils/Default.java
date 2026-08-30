package com.financia.kash.shared.domain.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
public @interface Default {

}
