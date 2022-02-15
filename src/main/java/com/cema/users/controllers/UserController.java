package com.cema.users.controllers;

import com.cema.users.constants.Messages;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the users database and authorization operations. V1")
@Validated
public class UserController {

    private static final String BASE_URL = "/users/";

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final CemaUserRepository cemaUserRepository;
    private final UserMapping userMapping;
    private final AuthorizationService authorizationService;
    private final UserValidationService userValidationService;
    private final AdministrationClientService administrationClientService;

    public UserController(CemaUserRepository cemaUserRepository, UserMapping userMapping,
                          AuthorizationService authorizationService, UserValidationService userValidationService,
                          AdministrationClientService administrationClientService) {
        this.cemaUserRepository = cemaUserRepository;
        this.userMapping = userMapping;
        this.authorizationService = authorizationService;
        this.userValidationService = userValidationService;
        this.administrationClientService = administrationClientService;
    }

    @ApiOperation(value = "Validate a user data by username", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found user"),
            @ApiResponse(code = 404, message = "The user you were looking for is not found"),
            @ApiResponse(code = 401, message = "You are not allowed to look for this user"),
            @ApiResponse(code = 422, message = "Invalid user")
    })
    @GetMapping(value = BASE_URL + "validate/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> validateUser(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName) {

        userName = userName.toLowerCase();
        LOG.info("Request for user {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser == null) {
            throw new NotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
        }
        if (!authorizationService.isOnTheSameEstablishment(cemaUser.getEstablishmentCuig())) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cemaUser.getEstablishmentCuig()));
        }
        User user = userMapping.mapEntityToDomain(cemaUser);

        userValidationService.validateUserForUsage(user);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieve a user data by username", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found user"),
            @ApiResponse(code = 404, message = "The user you were looking for is not found"),
            @ApiResponse(code = 401, message = "You are not allowed to look for this user")
    })
    @GetMapping(value = BASE_URL + "{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> lookUpUser(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName) {
        userName = userName.toLowerCase();
        LOG.info("Request for user {}", userName);

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser == null) {
            throw new NotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
        }

        if (!authorizationService.isOnTheSameEstablishment(cemaUser.getEstablishmentCuig())) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cemaUser.getEstablishmentCuig()));
        }

        String cuig = cemaUser.getEstablishmentCuig();

        User user = userMapping.mapEntityToDomain(cemaUser);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Register a new user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created successfully"),
            @ApiResponse(code = 409, message = "The user you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this user")
    })
    @PostMapping(value = BASE_URL + "register", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> register(
            @ApiParam(
                    value = "The password of the user we are looking for.",
                    example = "slipknot")
            @RequestParam("password") String password,
            @ApiParam(
                    value = "The user data we are trying to insert.")
            @RequestBody @Valid User user) {
        String userName = user.getUserName().toLowerCase();
        user.setUserName(userName);
        LOG.info("Request to register user: {}", userName);
        String cuig = user.getEstablishmentCuig();

        if (!authorizationService.isOnTheSameEstablishment(user.getEstablishmentCuig())) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        if (!authorizationService.isAdmin() && user.getRole().equalsIgnoreCase(Roles.ADMIN)) {
            throw new UnauthorizedException(String.format(Messages.ACTION_NOT_ALLOWED, user.getRole()));
        }

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser != null) {
            LOG.info("User already exists");
            throw new AlreadyExistsException(String.format(Messages.USER_ALREADY_EXISTS, userName));
        }
        cemaUser = userMapping.mapDomainToEntity(user, userName, password);

        cemaUserRepository.save(cemaUser);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Register a new user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created successfully"),
            @ApiResponse(code = 409, message = "The user you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this user")
    })
    @PutMapping(value = BASE_URL + "{username}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> updateUser(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String username,
            @ApiParam(
                    value = "The user data we are trying to insert.")
            @RequestBody  User user) {
        String userName = user.getUserName().toLowerCase();
        user.setUserName(userName);
        LOG.info("Request to update user: {}", userName);

        if (!authorizationService.isOnTheSameEstablishment(user.getEstablishmentCuig())) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, user.getEstablishmentCuig()));
        }
        if (!authorizationService.isAdmin() && user.getRole().equalsIgnoreCase(Roles.ADMIN)) {
            throw new UnauthorizedException(String.format(Messages.ACTION_NOT_ALLOWED, user.getRole()));
        }

        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser == null) {
            throw new NotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
        }
        cemaUser = userMapping.updateEntity(user, cemaUser);

        CemaUser cemaUserUpdated = cemaUserRepository.save(cemaUser);

        return ResponseEntity.ok(userMapping.mapEntityToDomain(cemaUserUpdated));
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Delete an existing user by username")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "The user you were trying to reach is not found"),
            @ApiResponse(code = 401, message = "You are not allowed to delete this user")
    })
    @DeleteMapping(value = BASE_URL + "{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> delete(
            @ApiParam(
                    value = "The username of the user we are looking for.",
                    example = "merlinds")
            @PathVariable("username") String userName) {

        LOG.info("Request to delete user: {}", userName);
        userName = userName.toLowerCase();
        CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
        if (cemaUser != null) {
            if (!authorizationService.isOnTheSameEstablishment(cemaUser.getEstablishmentCuig())) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cemaUser.getEstablishmentCuig()));
            }
            LOG.info("User exists, deleting");
            cemaUserRepository.delete(cemaUser);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Not found");
        throw new NotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Retrieve a list of users with the specified role", response = User.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found user")
    })
    @GetMapping(value = BASE_URL + "list/{role}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<User>> listUsersByRole(
            @ApiParam(
                    value = "The role of the users we are looking for.",
                    example = "patron")
            @PathVariable(value = "role") String role) {

        LOG.info("Searching users with role {}", role);

        List<CemaUser> bovineList = cemaUserRepository.findCemaUsersByRoleIgnoreCase(role);
        LOG.info("Returned {} users from db", bovineList.size());
        String currentCuig = authorizationService.getCurrentUserCuig();

        List<User> mappedUsers = bovineList.stream()
                .map(userMapping::mapEntityToDomain)
                .collect(Collectors.toList());

        //If the user is not admin we filter out external users
        if (!authorizationService.isAdmin()) {
            mappedUsers = mappedUsers.stream()
                    .filter(user -> user.getEstablishmentCuig().equals(currentCuig))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok().body(mappedUsers);
    }

}
