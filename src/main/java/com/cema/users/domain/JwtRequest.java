package com.cema.users.domain;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    @ApiModelProperty(notes = "The username for this user, needed for login", example = "merlinds")
    private String username;
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