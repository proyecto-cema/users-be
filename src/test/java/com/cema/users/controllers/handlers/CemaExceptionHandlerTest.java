package com.cema.users.controllers.handlers;

import com.cema.users.domain.ErrorResponse;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UnauthorizedException;
import com.cema.users.exceptions.AlreadyExistsException;
import com.cema.users.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@Slf4j
class CemaExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    public void setUp(){
        openMocks(this);
        webRequest = new ServletWebRequest(Mockito.mock(HttpServletRequest.class));
    }

    @Test
    public void handleUserNotFoundExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        NotFoundException ex = new NotFoundException("User pepito Not Found");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUserNotFoundException(ex, webRequest);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("User pepito Not Found"));
        assertThat(body.getDetails(), is("ServletWebRequest: uri=null"));
        assertThat(status, is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void handleInvalidCredentialsExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        InvalidCredentialsException ex = new InvalidCredentialsException("Incorrect credendials");

        ResponseEntity<Object> result = cemaExceptionHandler.handleInvalidCredentialsException(ex, webRequest);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Incorrect credendials"));
        assertThat(body.getDetails(), is("ServletWebRequest: uri=null"));
        assertThat(status, is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void handleUserExistsExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        AlreadyExistsException ex = new AlreadyExistsException("User pepito already exists");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUserExistsException(ex, webRequest);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("User pepito already exists"));
        assertThat(body.getDetails(), is("ServletWebRequest: uri=null"));
        assertThat(status, is(HttpStatus.CONFLICT));
    }

    @Test
    public void handleUnauthorizedExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        UnauthorizedException ex = new UnauthorizedException("Unauthorized");

        ResponseEntity<Object> result = cemaExceptionHandler.handleUnauthorizedException(ex, webRequest);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Unauthorized"));
        assertThat(body.getDetails(), is("ServletWebRequest: uri=null"));
        assertThat(status, is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void handleMethodArgumentNotValidExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        FieldError fieldError1 = new FieldError("objectName1", "field1", "defaultMessage1");
        FieldError fieldError2 = new FieldError("objectName2", "field2", "defaultMessage2");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<ErrorResponse> result = cemaExceptionHandler.handleMethodArgumentNotValidException(ex, webRequest);
        ErrorResponse body = result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Missing or incorrect fields"));
        assertThat(status, is(HttpStatus.BAD_REQUEST));
        assertThat(body.getViolations().size(), is(2));
    }

}