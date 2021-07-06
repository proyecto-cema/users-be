package com.cema.users.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class User {

    @ApiModelProperty(notes = "The username for this user, needed for login", example = "merlinds")
    private String userName;
    @ApiModelProperty(notes = "The name of this user", example = "Merlin")
    private String name;
    @ApiModelProperty(notes = "The last name of this user", example = "Nuñez")
    private String lastName;
    @ApiModelProperty(notes = "The phone number of this user", example = "3541330188")
    private String phone;
    @ApiModelProperty(notes = "The email address of this user", example = "merlinsn@gmail.com")
    private String email;
    @ApiModelProperty(notes = "The role of this user", example = "admin")
    private String role;
    @ApiModelProperty(notes = "When was this user created", hidden = true)
    private Date creationDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
