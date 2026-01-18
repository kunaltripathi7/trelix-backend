package com.trelix.trelix_app.validation;

import com.trelix.trelix_app.dto.request.CreateCommentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EitherTaskOrMessageValidator implements ConstraintValidator<EitherTaskOrMessage, CreateCommentRequest> {

    @Override
    public boolean isValid(CreateCommentRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        boolean hasTask = request.getTaskId() != null;
        boolean hasMessage = request.getMessageId() != null;

        return hasTask ^ hasMessage;
    }
}
