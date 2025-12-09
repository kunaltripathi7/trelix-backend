package com.trelix.trelix_app.exception;

import com.trelix.trelix_app.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
