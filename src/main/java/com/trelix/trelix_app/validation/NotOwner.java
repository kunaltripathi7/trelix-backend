package com.trelix.trelix_app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotOwnerValidator.class)
public @interface NotOwner {
    String message() default "Assigning OWNER role is not permitted.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
