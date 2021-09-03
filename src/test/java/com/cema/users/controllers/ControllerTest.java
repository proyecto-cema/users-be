package com.cema.users.controllers;

import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void registerShouldAlwaysReturnCreatedWhenUserAddedCorrectly() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        String password = "password";

        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);
        ResponseEntity<User> result = controller.register(password, user);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerShouldAlwaysReturnUnprocesableEntityWhenUserExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        String password = "password";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(loginService.login(password, cemaUser)).thenReturn(true);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);

        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);

        Exception exception = assertThrows(UserExistsException.class, () -> {
            controller.register(password, user);
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

        assertThat(resultingMessage, is("User otheruser doesn't exits"));
    }

    @Test
    public void listUsersByRoleShouldAlwaysReturnUsersWhenExists() {
        String role = "role";

        CemaUser cemaUser1 = new CemaUser();
        User user1 = new User();

        CemaUser cemaUser2 = new CemaUser();
        User user2 = new User();

        List<CemaUser> cemaUsers = Arrays.asList(cemaUser1, cemaUser2);

        when(cemaUserRepository.findCemaUsersByRole(role)).thenReturn(cemaUsers);
        when(userMapping.mapEntityToDomain(cemaUser1)).thenReturn(user1);
        when(userMapping.mapEntityToDomain(cemaUser2)).thenReturn(user2);
        Controller controller = new Controller(cemaUserRepository, userMapping, loginService);


        ResponseEntity<List<User>> result = controller.listUsersByRole(role);

        List<User> resultingUsers = result.getBody();

        assertTrue(resultingUsers.contains(user1));
        assertTrue(resultingUsers.contains(user2));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

}