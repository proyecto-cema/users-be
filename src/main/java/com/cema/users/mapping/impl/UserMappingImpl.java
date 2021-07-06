package com.cema.users.mapping.impl;

import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.mapping.UserMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class UserMappingImpl implements UserMapping {

    @Override
    public User mapEntityToDomain(CemaUser cemaUser) {
        User user = new User();
        user.setName(cemaUser.getName());
        user.setUserName(cemaUser.getUserName());
        user.setLastName(cemaUser.getLastName());
        user.setEmail(cemaUser.getEmail());
        user.setPhone(cemaUser.getPhone());
        user.setRole(cemaUser.getRole());
        user.setCreationDate(cemaUser.getCreationDate());

        return user;
    }

    @Override
    public CemaUser mapDomainToEntity(User user, String userName, String password) {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setName(user.getName());
        cemaUser.setUserName(userName);
        cemaUser.setLastName(user.getLastName());
        cemaUser.setEmail(user.getEmail());
        cemaUser.setPhone(user.getPhone());
        cemaUser.setRole(user.getRole());
        cemaUser.setCreationDate(new Date());
        String hashedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        cemaUser.setPassword(hashedPassword);

        return cemaUser;
    }
}
