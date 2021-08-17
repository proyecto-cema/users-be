package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CemaExceptionHandlerTest {

    @Test
    public void handleUserNotFoundExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
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
    public void handleInvalidCredentialsExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        InvalidCredentialsException ex = new InvalidCredentialsException("Incorrect credendtials");

        ResponseEntity<Object> result = cemaExceptionHandler.handleInvalidCredentialsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Invalid credentials"));
        assertThat(body.getDetails(), is("Incorrect credendtials"));
        assertThat(status, is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void handleUserExistsExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        UserExistsException ex = new UserExistsException("User pepito already exists");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUserExistsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("User Exists"));
        assertThat(body.getDetails(), is("User pepito already exists"));
        assertThat(status, is(HttpStatus.CONFLICT));
    }

}