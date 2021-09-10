package com.cema.users.domain;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class User {

    @ApiModelProperty(notes = "The cuig of the establishment this user belongs to", example = "312")
    @NotEmpty(message = "Establishment is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "The username for this user, needed for login", example = "merlinds")
    @NotEmpty(message = "Username is required")
    private String userName;
    @ApiModelProperty(notes = "The name of this user", example = "Merlin")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The last name of this user", example = "Nuñez")
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @ApiModelProperty(notes = "The phone number of this user", example = "3541330188")
    private String phone;
    @ApiModelProperty(notes = "The email address of this user", example = "merlinsn@gmail.com")
    private String email;
    @ApiModelProperty(notes = "The role of this user", example = "admin")
    @NotEmpty(message = "Role name is required")
    @Pattern(regexp = "(?i)admin|peon|patron")
    private String role;
    @ApiModelProperty(notes = "When was this user created", hidden = true)
    private Date creationDate;

    public String getEstablishmentCuig() {
        return establishmentCuig;
    }

    public void setEstablishmentCuig(String establishmentCuig) {
        this.establishmentCuig = establishmentCuig;
    }

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

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
