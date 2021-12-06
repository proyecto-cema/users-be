package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UnauthorizedException;
import com.cema.users.exceptions.AlreadyExistsException;
import com.cema.users.exceptions.NotFoundException;
import com.cema.users.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class CemaExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(NotFoundException ex, WebRequest request) {
        log.error("Exception while processing.", ex);

        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public final ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        log.error("Exception while processing.", ex);

        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Exception while processing.", ex);

        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public final ResponseEntity<Object> handleUserExistsException(AlreadyExistsException ex, WebRequest request) {
        log.error("Exception while processing.", ex);

        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        log.error("Exception while processing.", ex);

        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = "Missing or incorrect fields";
        ErrorResponse error = new ErrorResponse(message, request.toString());

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new ErrorResponse.Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        log.error("Exception while processing.", ex);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), request.toString());
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}
