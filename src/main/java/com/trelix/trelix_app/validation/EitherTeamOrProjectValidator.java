package com.trelix.trelix_app.validation;

import com.trelix.trelix_app.dto.CreateTaskRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EitherTeamOrProjectValidator implements ConstraintValidator<EitherTeamOrProject, CreateTaskRequest> {

    @Override
    public boolean isValid(CreateTaskRequest request, ConstraintValidatorContext context) {
        if (request == null) return true; // for other validators to handle null if it is there.
        return (request.teamId() == null) != (request.projectId() == null);
    }
}
