package com.trelix.trelix_app.exception;

public class ResourceNotFoundException extends RuntimeException{
        public ResourceNotFoundException(String message) {
            super(message);
        }
}
