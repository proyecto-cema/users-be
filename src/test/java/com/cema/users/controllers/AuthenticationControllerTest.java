package com.cema.users.controllers;

import com.cema.users.domain.JwtRequest;
import com.cema.users.domain.JwtResponse;
import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UserNotFoundException;
import com.cema.users.mapping.UserMapping;
import com.cema.users.repositories.CemaUserRepository;
import com.cema.users.services.jwt.TokenService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class AuthenticationControllerTest {

    @Captor
    public ArgumentCaptor<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationTokenArgumentCaptor;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenServiceImpl;
    @Mock
    private UserDetailsService userDetailsServiceImpl;
    @Mock
    private CemaUserRepository cemaUserRepository;
    @Mock
    private UserMapping userMapping;

    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        authenticationController = new AuthenticationController(authenticationManager, tokenServiceImpl,
                userDetailsServiceImpl, cemaUserRepository, userMapping);
    }

    @Test
    public void getUserDataFromTokenShouldReturnUserDataWhenCorrectTokenPassed() {
        String prefix = "Bearer ";
        String token = "sometoken";
        String userName = "userName";

        CemaUser cemaUser = new CemaUser();
        User user = new User();

        when(tokenServiceImpl.getUsernameFromToken(token)).thenReturn(userName);

        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);

        ResponseEntity<User> result = authenticationController.getUserDataFromToken(prefix + token);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody(), is(user));
    }
    @Test
    public void getUserDataFromTokenShouldThrowInvalidCredentialsExceptionWhenIncorrectTokenPassed() {
        String token = "sometoken";

        Assert.assertThrows("Invalid token: " + token, InvalidCredentialsException.class, () -> authenticationController.getUserDataFromToken(token));
    }

    @Test
    public void getUserDataFromTokenShouldThrowInvalidCredentialsExceptionWhenEmptyTokenPassed() {
        String token = "";

        Assert.assertThrows("Invalid token: " + token, InvalidCredentialsException.class, () -> authenticationController.getUserDataFromToken(token));
    }

    @Test
    public void getUserDataFromTokenShouldThrowInvalidCredentialsExceptionWhenNullTokenPassed() {
        String token = null;

        Assert.assertThrows("Invalid token: " + token, InvalidCredentialsException.class, () -> authenticationController.getUserDataFromToken(token));
    }

    @Test
    public void getUserDataFromTokenShouldThrowUserNotFoundExceptionWhenUserDoesNotExists() {
        String prefix = "Bearer ";
        String token = "sometoken";
        String userName = "userName";

        when(tokenServiceImpl.getUsernameFromToken(token)).thenReturn(userName);

        Assert.assertThrows("User userName doesn't exits", UserNotFoundException.class, () -> authenticationController.getUserDataFromToken(prefix + token));
    }

    @Test
    public void createAuthenticationTokenShouldReturnJwtResponseWithToken() {
        JwtRequest authenticationRequest = new JwtRequest();
        String userName = "username";
        String password = "password";
        authenticationRequest.setUsername(userName);
        authenticationRequest.setPassword(password);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(usernamePasswordAuthenticationTokenArgumentCaptor.capture())).thenReturn(authentication);

        CemaUser cemaUser = new CemaUser();

        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);

        User user = new User();

        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsServiceImpl.loadUserByUsername(userName)).thenReturn(userDetails);

        String token = "token";

        when(tokenServiceImpl.generateToken(userDetails)).thenReturn(token);

        ResponseEntity<JwtResponse> result = authenticationController.createAuthenticationToken(authenticationRequest);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        JwtResponse jwtResponse = result.getBody();

        assertThat(jwtResponse.getToken(), is(token));
        assertThat(jwtResponse.getUser(), is(user));
    }

    @Test
    public void createAuthenticationTokenShouldThrowInvalidCredentialsExceptionWhenAuthenticationFails() {
        JwtRequest authenticationRequest = new JwtRequest();
        String userName = "userName";
        String password = "password";
        authenticationRequest.setUsername(userName);
        authenticationRequest.setPassword(password);

        when(authenticationManager.authenticate(usernamePasswordAuthenticationTokenArgumentCaptor.capture())).thenThrow(BadCredentialsException.class);

        Assert.assertThrows("Invalid credentials", InvalidCredentialsException.class, () -> authenticationController.createAuthenticationToken(authenticationRequest));
    }

}