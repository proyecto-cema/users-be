package com.cema.users.mapping.impl;


import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.mapping.UserMapping;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class UserMappingImplTest {

    private UserMapping userMapping = new UserMappingImpl();

    @Test
    public void mapEntityToDomainShouldReturnCorrectDomainObject(){
        String userName = "userName";
        String name = "name";
        String lastName = "lastName";
        String role = "role";
        String phone = "phone";
        String email = "email";
        Date creationDate = new Date();
        CemaUser cemaUser = new CemaUser();
        cemaUser.setUserName(userName);
        cemaUser.setName(name);
        cemaUser.setLastName(lastName);
        cemaUser.setRole(role);
        cemaUser.setEmail(email);
        cemaUser.setCreationDate(creationDate);
        cemaUser.setPhone(phone);

        User resultUser = userMapping.mapEntityToDomain(cemaUser);

        assertThat(resultUser.getUserName(), is(userName));
        assertThat(resultUser.getName(), is(name));
        assertThat(resultUser.getLastName(), is(lastName));
        assertThat(resultUser.getRole(), is(role));
        assertThat(resultUser.getEmail(), is(email));
        assertThat(resultUser.getCreationDate(), is(creationDate));
        assertThat(resultUser.getPhone(), is(phone));
    }

    @Test
    public void mapDomainToEntityShouldReturnCorrectEntityObject(){
        String userName = "userName";
        String name = "name";
        String lastName = "lastName";
        String role = "role";
        String phone = "phone";
        String email = "email";
        String password = "password";
        String hashedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setRole(role);
        user.setEmail(email);
        user.setPhone(phone);

        CemaUser cemaUser = userMapping.mapDomainToEntity(user, userName, password);

        assertThat(cemaUser.getUserName(), is(userName));
        assertThat(cemaUser.getName(), is(name));
        assertThat(cemaUser.getLastName(), is(lastName));
        assertThat(cemaUser.getRole(), is(role));
        assertThat(cemaUser.getEmail(), is(email));
        assertThat(cemaUser.getPhone(), is(phone));
        assertThat(cemaUser.getPassword(), is(hashedPassword));

    }

}