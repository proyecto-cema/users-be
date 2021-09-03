package com.cema.users.domain;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

public class JwtRequest {

    @ApiModelProperty(notes = "The username for this user, needed for login", example = "merlinds")
    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "Password is required")
    @ApiModelProperty(notes = "The password for this user, needed for login", example = "verysecure")
    private String password;

    //need default constructor for JSON Parsing
    public JwtRequest()
    {

    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}