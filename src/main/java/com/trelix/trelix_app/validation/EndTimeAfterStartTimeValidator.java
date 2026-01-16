package com.trelix.trelix_app.validation;

import com.trelix.trelix_app.dto.request.CreateEventRequest;
import com.trelix.trelix_app.dto.request.UpdateEventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof CreateEventRequest request) {
            if (request.startTime() == null || request.endTime() == null) {
                return true; // Let @NotNull handle null checks
            }
            return request.endTime().isAfter(request.startTime());
        } else if (obj instanceof UpdateEventRequest request) {
            if (request.startTime() == null || request.endTime() == null) {
                return true; // Let @NotNull handle null checks
            }
            return request.endTime().isAfter(request.startTime());
        }

        return false; // Should not happen if annotation is used correctly
    }
}




