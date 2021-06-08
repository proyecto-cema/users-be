package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.IncorrectCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CemaExceptionHandlerTest {

    @Test
    public void handleUserNotFoundExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        UserNotFoundException ex = new UserNotFoundException("User pepito Not Found");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUserNotFoundException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("User Not Found"));
        assertThat(body.getDetails(), is("User pepito Not Found"));
        assertThat(status, is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void handleUserExistsExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        UserExistsException ex = new UserExistsException("User pepito already exists");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUserExistsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("User Exists"));
        assertThat(body.getDetails(), is("User pepito already exists"));
        assertThat(status, is(HttpStatus.CONFLICT));
    }

    @Test
    public void handleIncorrectCredentialsExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        IncorrectCredentialsException ex = new IncorrectCredentialsException("Password for user pepito is incorrect");

        ResponseEntity<Object> result = cemaExceptionHandler.handleIncorrectCredentialsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Incorrect user and/or password"));
        assertThat(body.getDetails(), is("Password for user pepito is incorrect"));
        assertThat(status, is(HttpStatus.UNAUTHORIZED));
    }

}