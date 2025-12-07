package com.trelix.trelix_app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
    String message() default "Password must be at least 8 characters with 1 uppercase, 1 lowercase, and 1 digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
