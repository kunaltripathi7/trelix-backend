package com.trelix.trelix_app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EitherTeamOrProjectValidator.class)
public @interface EitherTeamOrProject {
    String message() default "Either teamId or projectId must be provided, but not both.";

    Class<?>[] groups() default {}; // when you need to do some modification in this like you need pass on create but not update you can specify the group name in the requestDTO.

    Class<? extends Payload>[] payload() default {};
}
