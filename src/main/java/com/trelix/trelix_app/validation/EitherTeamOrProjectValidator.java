package com.trelix.trelix_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// first is the trigger annotaion that will trigger and 2nd is the data on which we will perform the action.
public class EitherTeamOrProjectValidator implements ConstraintValidator<EitherTeamOrProject, TeamProjectAware> {

    @Override // without interface -> doesn't know hwo to access the value
    public boolean isValid(TeamProjectAware value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        boolean hasTeam = value.teamId() != null;
        boolean hasProject = value.projectId() != null;

        return hasTeam ^ hasProject;
    }
}