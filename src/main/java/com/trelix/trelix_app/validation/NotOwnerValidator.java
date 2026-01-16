package com.trelix.trelix_app.validation;

import com.trelix.trelix_app.enums.TeamRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotOwnerValidator implements ConstraintValidator<NotOwner, TeamRole> {

    @Override
    public boolean isValid(TeamRole role, ConstraintValidatorContext context) {
        if (role == null) {
            return true;
        }
        return role != TeamRole.OWNER;
    }
}




