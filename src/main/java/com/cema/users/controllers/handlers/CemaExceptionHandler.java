package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.IncorrectCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CemaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("User Not Found", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExistsException.class)
    public final ResponseEntity<Object> handleUserExistsException(UserExistsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("User Exists", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public final ResponseEntity<Object> handleIncorrectCredentialsException(IncorrectCredentialsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Incorrect user and/or password", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }
}
