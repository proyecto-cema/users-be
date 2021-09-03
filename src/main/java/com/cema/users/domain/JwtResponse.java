package com.cema.users.domain;

import io.swagger.annotations.ApiModelProperty;

public class JwtResponse {
    @ApiModelProperty(notes = "The token representing this session")
    private final String token;
    private User user;

    public JwtResponse(String token) {
        this.token = token;
    }

    public JwtResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}