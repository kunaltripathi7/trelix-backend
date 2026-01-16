package com.trelix.trelix_app.exception;

import com.trelix.trelix_app.dto.response.ErrorResponse;
import com.trelix.trelix_app.dto.response.ValidationErrorResponse;
import com.trelix.trelix_app.enums.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        private ErrorResponse buildErrorResponse(String message, ErrorCode errorCode, String path) {
                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now().toString())
                                .path(path)
                                .errorCode(errorCode.name())
                                .message(message)
                                .build();
        }

        @ExceptionHandler(InvalidRequestException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ex.getErrorCode(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                ValidationErrorResponse errorResponse = new ValidationErrorResponse();
                errorResponse.setTimestamp(LocalDateTime.now().toString());
                errorResponse.setPath(request.getRequestURI());
                errorResponse.setErrorCode(ErrorCode.VALIDATION_FAILED.name());
                errorResponse.setMessage("Validation failed for one or more fields.");

                Map<String, String> validationErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(fieldError -> validationErrors.put(fieldError.getField(),
                                                fieldError.getDefaultMessage()));

                errorResponse.setFieldErrors(validationErrors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                logger.warn("Malformed JSON request: {}", ex.getMessage());
                ErrorResponse errorResponse = buildErrorResponse(
                                "Malformed JSON request. Check the request body for syntax errors.",
                                ErrorCode.MALFORMED_JSON,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex,
                        HttpServletRequest request) {
                String message = "Required parameter '" + ex.getParameterName() + "' is missing.";
                ErrorResponse errorResponse = buildErrorResponse(message, ErrorCode.INVALID_REQUEST_PARAMETER,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {
                String requiredType = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName)
                                .orElse("unknown");
                String message = "Parameter '" + ex.getName() + "' should be of type " + requiredType + ".";
                ErrorResponse errorResponse = buildErrorResponse(message, ErrorCode.INVALID_PATH_VARIABLE,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                        HttpServletRequest request) {
                logger.error("Database integrity violation: {}", ex.getMessage());
                ErrorResponse errorResponse = buildErrorResponse(
                                "A database conflict occurred. This may be due to a duplicate entry or a foreign key constraint failure.",
                                ErrorCode.DATABASE_CONFLICT, request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ex.getErrorCode(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(ServiceException.class)
        public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
                logger.error("External service failure: {}", ex.getMessage(), ex);
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ex.getErrorCode(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex,
                        HttpServletRequest request) {
                String message = "File size exceeds the maximum allowed limit. " + ex.getMessage();
                ErrorResponse errorResponse = buildErrorResponse(message, ErrorCode.FILE_UPLOAD_MAX_SIZE_EXCEEDED,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex,
                        HttpServletRequest request) {
                String message = "The requested endpoint '" + ex.getRequestURL() + "' could not be found.";
                ErrorResponse errorResponse = buildErrorResponse(message, ErrorCode.ENDPOINT_NOT_FOUND,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ErrorCode.UNAUTHORIZED_ACCESS,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), ex.getErrorCode(),
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
                logger.error("An unexpected error occurred at path: {}", request.getRequestURI(), ex);
                ErrorResponse errorResponse = buildErrorResponse(
                                "An unexpected internal error occurred. Please contact support.",
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                request.getRequestURI());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
