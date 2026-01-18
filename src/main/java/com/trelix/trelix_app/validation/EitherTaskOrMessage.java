package com.trelix.trelix_app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EitherTaskOrMessageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EitherTaskOrMessage {
    String message() default "Comment must be associated with either a task or a message, but not both.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
