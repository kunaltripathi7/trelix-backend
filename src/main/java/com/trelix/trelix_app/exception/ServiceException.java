package com.trelix.trelix_app.exception;

import com.trelix.trelix_app.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServiceException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}




