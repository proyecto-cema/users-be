package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CemaExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("User Not Found", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public final ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Invalid credentials", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserExistsException.class)
    public final ResponseEntity<Object> handleUserExistsException(UserExistsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("User Exists", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();
        String message = "Missing Fields";
        error.setMessage(message);
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new ErrorResponse.Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}
