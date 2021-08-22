package com.cema.users.controllers;

import com.cema.users.constants.Constants;
import com.cema.users.constants.Messages;
import com.cema.users.domain.JwtRequest;
import com.cema.users.domain.JwtResponse;
import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.InvalidCredentialsException;
import com.cema.users.exceptions.UserNotFoundException;
import com.cema.users.mapping.UserMapping;
import com.cema.users.repositories.CemaUserRepository;
import com.cema.users.services.jwt.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the users database and authorization operations. V1")
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenServiceImpl;

    private final UserDetailsService userDetailsServiceImpl;

    private final CemaUserRepository cemaUserRepository;

    private final UserMapping userMapping;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenServiceImpl, UserDetailsService userDetailsServiceImpl, CemaUserRepository cemaUserRepository, UserMapping userMapping) {
        this.authenticationManager = authenticationManager;
        this.tokenServiceImpl = tokenServiceImpl;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.cemaUserRepository = cemaUserRepository;
        this.userMapping = userMapping;
    }

    @ApiOperation(value = "Retrieve a user data by jwt token", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found user"),
            @ApiResponse(code = 401, message = "Invalid token")
    })
    @PostMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> getUserDataFromToken(@RequestHeader("Authorization") String token) {
        LOG.info("Request for token {}", token);
        if(StringUtils.hasText(token) && token.startsWith(Constants.BEARER_PREFIX)) {
            token = token.substring(7);
            String userName = tokenServiceImpl.getUsernameFromToken(token);

            CemaUser cemaUser = cemaUserRepository.findCemaUserByUserName(userName);
            if (cemaUser == null) {
                throw new UserNotFoundException(String.format(Messages.USER_DOES_NOT_EXISTS, userName));
            }
            User user = userMapping.mapEntityToDomain(cemaUser);
            LOG.info("Returning user: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new InvalidCredentialsException("Invalid token: " + token);
        }
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @ApiOperation(value = "Login as a user and retrieve an auth token for further usage", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully login"),
            @ApiResponse(code = 401, message = "The provided credentials are incorrect")
    })
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        LOG.info("Authentication request for user: {}", authenticationRequest.getUsername());
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(authenticationRequest.getUsername());

        String token = tokenServiceImpl.generateToken(userDetails);
        LOG.info("Returning token: {}", token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            LOG.error("Error while authenticating user", e);
            throw new InvalidCredentialsException("Invalid credentials", e);
        }
    }
}