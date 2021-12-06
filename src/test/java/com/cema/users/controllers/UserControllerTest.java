package com.cema.users.controllers;

import com.cema.users.constants.Roles;
import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.AlreadyExistsException;
import com.cema.users.exceptions.NotFoundException;
import com.cema.users.exceptions.UnauthorizedException;
import com.cema.users.mapping.UserMapping;
import com.cema.users.repositories.CemaUserRepository;
import com.cema.users.services.authorization.AuthorizationService;
import com.cema.users.services.validation.UserValidationService;
import com.cema.users.services.validation.administration.AdministrationClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserControllerTest {

    @Mock
    private CemaUserRepository cemaUserRepository;
    @Mock
    private UserMapping userMapping;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private AdministrationClientService administrationClientService;

    private UserController userController;

    private String cuig = "321";

    @BeforeEach
    public void startUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        userController = new UserController(cemaUserRepository, userMapping, authorizationService, userValidationService,
                administrationClientService);
    }

    @Test
    public void lookUpUserShouldAlwaysReturnUserWhenExists() {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(cuig);
        User user = new User();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);

        ResponseEntity<User> result = userController.lookUpUser(userName);
        User resultingUser = result.getBody();

        assertThat(resultingUser, is(user));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpUserShouldAlwaysReturnNotFoundWhenUserDoesntExists() {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(cuig);
        User user = new User();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userController.lookUpUser("otheruser");
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("User otheruser doesn't exits"));
    }

    @Test
    public void lookUpUserShouldThrowUnauthorizedExceptionWhenAnExternalCuigIsRequested() {
        String differentCuig = "0000";
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(differentCuig);
        User user = new User();
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapEntityToDomain(cemaUser)).thenReturn(user);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userController.lookUpUser(userName);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to access resource from a different establishment 0000."));
    }

    @Test
    public void registerShouldAlwaysReturnCreatedWhenUserAddedCorrectly() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        user.setEstablishmentCuig(cuig);
        user.setRole(Roles.PATRON);
        String password = "password";

        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);

        ResponseEntity<User> result = userController.register(password, user);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerShouldAlwaysReturnCreatedWhenAdminUserAddedCorrectlyWhileBeingAdmin() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        user.setEstablishmentCuig(cuig);
        user.setRole(Roles.ADMIN);
        String password = "password";

        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);
        when(authorizationService.isAdmin()).thenReturn(true);

        ResponseEntity<User> result = userController.register(password, user);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerShouldAlwaysReturnUnprocesableEntityWhenUserExists() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        user.setEstablishmentCuig(cuig);
        String password = "password";
        user.setRole(Roles.PATRON);
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);


        Exception exception = assertThrows(AlreadyExistsException.class, () -> {
            userController.register(password, user);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("The user merlin already exists"));
    }

    @Test
    public void registerShouldThrowUnauthorizedExceptionWhenAnExternalCuigIsRequested() {
        String otherCuig = "000";
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        user.setEstablishmentCuig(otherCuig);
        String password = "password";
        user.setRole(Roles.PATRON);
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);


        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userController.register(password, user);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to access resource from a different establishment 000."));
    }

    @Test
    public void registerShouldThrowUnauthorizedExceptionWhenTryingToRegisterAdminWhileNotAdmin() {
        CemaUser cemaUser = new CemaUser();
        User user = new User();
        String userName = "merlin";
        user.setUserName(userName);
        user.setEstablishmentCuig(cuig);
        String password = "password";
        user.setRole(Roles.ADMIN);
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);
        when(userMapping.mapDomainToEntity(user, userName, password)).thenReturn(cemaUser);
        when(authorizationService.isAdmin()).thenReturn(false);


        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userController.register(password, user);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to perform action your rol ADMIN is not authorized for."));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenUserExists() {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(cuig);
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);


        ResponseEntity<User> result = userController.delete(userName);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(cuig);
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userController.delete("otherUser");
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("User otheruser doesn't exits"));
    }

    @Test
    public void deleteShouldThrowUnauthorizedExceptionWhenAnExternalCuigIsRequested() {
        String otherCuig = "000";
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(otherCuig);
        String userName = "merlin";
        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(cemaUser);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userController.delete(userName);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to access resource from a different establishment 000."));
    }

    @Test
    public void listUsersByRoleShouldAlwaysReturnAllUsersWhenExistsWhileAdmin() {
        String role = "role";
        String otherCuig = "00";

        CemaUser cemaUser1 = new CemaUser();
        User user1 = new User();
        user1.setEstablishmentCuig(otherCuig);

        CemaUser cemaUser2 = new CemaUser();
        User user2 = new User();
        user2.setEstablishmentCuig(cuig);


        List<CemaUser> cemaUsers = Arrays.asList(cemaUser1, cemaUser2);

        when(cemaUserRepository.findCemaUsersByRoleIgnoreCase(role)).thenReturn(cemaUsers);
        when(userMapping.mapEntityToDomain(cemaUser1)).thenReturn(user1);
        when(userMapping.mapEntityToDomain(cemaUser2)).thenReturn(user2);
        when(authorizationService.isAdmin()).thenReturn(true);


        ResponseEntity<List<User>> result = userController.listUsersByRole(role);

        List<User> resultingUsers = result.getBody();

        assertTrue(resultingUsers.contains(user1));
        assertTrue(resultingUsers.contains(user2));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void listUsersByRoleShouldAlwaysReturnUsersWithSameCuigWhenExists() {
        String role = "role";
        String otherCuig = "00";

        CemaUser cemaUser1 = new CemaUser();
        User user1 = new User();
        user1.setEstablishmentCuig(cuig);

        CemaUser cemaUser2 = new CemaUser();
        User user2 = new User();
        user2.setEstablishmentCuig(otherCuig);

        List<CemaUser> cemaUsers = Arrays.asList(cemaUser1, cemaUser2);

        when(cemaUserRepository.findCemaUsersByRoleIgnoreCase(role)).thenReturn(cemaUsers);
        when(userMapping.mapEntityToDomain(cemaUser1)).thenReturn(user1);
        when(userMapping.mapEntityToDomain(cemaUser2)).thenReturn(user2);


        ResponseEntity<List<User>> result = userController.listUsersByRole(role);

        List<User> resultingUsers = result.getBody();

        assertTrue(resultingUsers.contains(user1));
        assertFalse(resultingUsers.contains(user2));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

}