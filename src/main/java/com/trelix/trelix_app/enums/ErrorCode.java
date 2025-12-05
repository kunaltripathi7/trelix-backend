package com.trelix.trelix_app.enums;

public enum ErrorCode {
    // --- General & Framework Errors ---
    INTERNAL_SERVER_ERROR,      // Fallback for any unhandled exceptions
    VALIDATION_FAILED,          // For @Valid failures
    MALFORMED_JSON,             // For HttpMessageNotReadableException
    METHOD_NOT_SUPPORTED,       // For HttpRequestMethodNotSupportedException
    INVALID_REQUEST_PARAMETER,  // For MissingServletRequestParameterException
    INVALID_PATH_VARIABLE,      // For MethodArgumentTypeMismatchException
    ENDPOINT_NOT_FOUND,         // For NoHandlerFoundException

    // --- Database & Persistence Errors ---
    DATABASE_ERROR,             // General DataAccessException
    DATABASE_CONFLICT,          // For DataIntegrityViolationException
    RESOURCE_NOT_FOUND,         // For EntityNotFoundException or our custom checks

    // --- Security & Authorization Errors ---
    UNAUTHORIZED_ACCESS,        // For AccessDeniedException
    AUTHENTICATION_FAILURE,     // For authentication-related issues

    // --- File & Upload Errors ---
    FILE_UPLOAD_MAX_SIZE_EXCEEDED,
    ATTACHMENT_UPLOAD_FAILED,

    // --- Custom Business Logic Errors ---
    INVALID_INPUT,              // Generic business rule violation
    MISSING_TASK_OR_MESSAGE_ID,  // Specific example from AttachmentController
    MUTUALLY_EXCLUSIVE_PARAMETERS,  // Specific example from AttachmentController
}
