package com.cema.users.controllers;

import com.cema.users.constants.Messages;
import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.IncorrectCredentialsException;
import com.cema.users.exceptions.UserExistsException;
import com.cema.users.exceptions.UserNotFoundException;
import com.cema.users.mapping.UserMapping;
import com.cema.users.repositories.CemaUserRepository;
import com.cema.users.services.login.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the users database and authorization operations. V1")
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final CemaUserRepository cemaUserRepository;
    private final UserMapping userMapping;
    private final LoginService loginService;

    public Controller(CemaUserRepository cemaUserRepository, UserMapping userMapping, LoginService loginService) {
        this.cemaUserRepository = cemaUserRepository;
        this.userMapping = userMapping;
        this.loginService = loginService;
    }

    @ApiOperation(value = "Retrieve a user data by username", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found user"),
            @ApiResponse(code = 404, message = "The user you were looking for is not found")
    })
    @GetMapping(value = "/users/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> lookUpUser(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName) {

        LOG.info("Request for user {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser == null) {
            throw new UserNotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
        }
        User user = userMapping.mapEntityToDomain(cemaUser);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Login as a user and retrieve its data with username and password", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully login"),
            @ApiResponse(code = 404, message = "The user you were looking for is not found"),
            @ApiResponse(code = 401, message = "The provided credentials are incorrect")
    })
    @GetMapping(value = "/users/login/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> login(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName,
            @ApiParam(
                    value = "The password of the user we are looking for.",
                    example = "slipknot")
            @RequestParam("password") String password) {

        LOG.info("Request to login user: {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser == null) {
            LOG.info("User not found in database");
            throw new UserNotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
        }
        if (loginService.login(password, cemaUser)) {
            User user = userMapping.mapEntityToDomain(cemaUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            LOG.info("Incorrect credentials");
            throw new IncorrectCredentialsException(String.format(Messages.INCORRECT_CREDENTIALS, userName));
        }
    }

    @ApiOperation(value = "Register a new user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created successfully"),
            @ApiResponse(code = 409, message = "The user you were trying to create already exists")
    })
    @PutMapping(value = "/users/{username}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> register(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName,
            @ApiParam(
                    value = "The password of the user we are looking for.",
                    example = "slipknot")
            @RequestParam("password") String password,
            @ApiParam(
                    value = "The user data we are trying to insert.")
            @RequestBody User user) {

        LOG.info("Request to register user: {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser != null) {
            LOG.info("User already exists");
            throw new UserExistsException(String.format(Messages.USER_ALREADY_EXISTS, userName));
        }

        cemaUser = userMapping.mapDomainToEntity(user, userName, password);

        cemaUserRepository.save(cemaUser);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete an existing user by username")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "The user you were trying to reach is not found")
    })
    @DeleteMapping(value = "/users/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> delete(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName) {

        LOG.info("Request to delete user: {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser != null) {
            LOG.info("User exists, deleting");
            cemaUserRepository.delete(cemaUser);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Not found");
        throw new UserNotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
    }

}
