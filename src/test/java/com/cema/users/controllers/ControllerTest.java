package com.cema.users.controllers;

import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.IncorrectCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import com.cema.users.mapping.UserMapping;
import com.cema.users.repositories.CemaUserRepository;
import com.cema.users.services.login.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ControllerTest {

    @Mock
    private CemaUserRepository cemaUserRepository;
    @Mock
    private UserMapping userMapping;
    @Mock
    private LoginService loginService;

    @BeforeEach
    public void startUp() {
        openMocks(this);
    }

    @Test
    public void lookUpUserShouldAlwaysReturnUserWhenExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        ResponseEntity<User> result = controller.lookUpUser(userName);
        User resultingUser = result.getBody();

        assertThat(resultingUser, is(user));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpUserShouldAlwaysReturnNotFoundWhenUserDoesntExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            controller.lookUpUser("otheruser");
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("User otheruser doesn't exits"));
    }

    @Test
    public void loginShouldAlwaysReturnUserWhenExistsAndPasswordIsCorrect() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        String password = "password";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        ResponseEntity<User> result = controller.login(userName, password);
        User resultingUser = result.getBody();

        assertThat(resultingUser, is(user));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void loginShouldAlwaysReturnUnauthorizedWhenPasswordIsIncorrect() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        String password = "password";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        Exception exception = assertThrows(IncorrectCredentialsException.class, () -> {
            controller.login(userName, "otherpassword");
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Incorrect credentials for user merlin"));
    }


    @Test
    public void loginShouldAlwaysReturnNotFoundWhenUserDoesntExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        String password = "password";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
                    controller.login("otheruser", password);
                });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("User otheruser doesn't exits"));
    }

    @Test
    public void registerShouldAlwaysReturnCreatedWhenUserAddedCorrectly() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        String password = "password";

        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        ResponseEntity<User> result = controller.register(userName, password, user);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerShouldAlwaysReturnUnprocesableEntityWhenUserExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        String password = "password";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);

        Exception exception = assertThrows(UserExistsException.class, () -> {
            controller.register(userName, password, user);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("The user merlin already exists"));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenUserExists() {
        CemaUser cemaUser = new CemaUser();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);

        ResponseEntity<User> result = controller.delete(userName);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaUser cemaUser = new CemaUser();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            controller.delete("otherUser");
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("User otherUser doesn't exits"));
    }

}