package com.trelix.trelix_app.exception;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
        public ResourceNotFoundException(String message) {
            super(message);
        }
}
