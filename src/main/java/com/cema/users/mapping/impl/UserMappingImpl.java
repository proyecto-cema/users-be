package com.cema.users.mapping.impl;

import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;
import com.cema.users.mapping.UserMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class UserMappingImpl implements UserMapping {

    private final PasswordEncoder bcryptEncoder;

    public UserMappingImpl(PasswordEncoder bcryptEncoder) {
        this.bcryptEncoder = bcryptEncoder;
    }

    @Override
    public User mapEntityToDomain(CemaUser cemaUser) {
        User user = new User();
        user.setEstablishmentCuig(cemaUser.getEstablishmentCuig());
        user.setName(cemaUser.getName());
        user.setUserName(cemaUser.getUserName());
        user.setLastName(cemaUser.getLastName());
        user.setEmail(cemaUser.getEmail());
        user.setPhone(cemaUser.getPhone());
        user.setRole(cemaUser.getRole());
        user.setCreationDate(cemaUser.getCreationDate());
        user.setEnabled(cemaUser.getEnabled());

        return user;
    }

    @Override
    public CemaUser mapDomainToEntity(User user, String userName, String password) {
        CemaUser cemaUser = new CemaUser();
        cemaUser.setEstablishmentCuig(user.getEstablishmentCuig());
        cemaUser.setName(user.getName());
        cemaUser.setUserName(userName);
        cemaUser.setLastName(user.getLastName());
        cemaUser.setEmail(user.getEmail());
        cemaUser.setPhone(user.getPhone());
        cemaUser.setRole(user.getRole().toUpperCase());
        cemaUser.setCreationDate(new Date());
        cemaUser.setPassword(bcryptEncoder.encode(password));
        cemaUser.setEnabled(user.getEnabled() != null ? user.getEnabled() : true);

        return cemaUser;
    }

    @Override
    public CemaUser updateEntity(User user, CemaUser cemaUser) {
        String name = StringUtils.hasText(user.getName()) ? user.getName() : cemaUser.getName();
        String lastName = StringUtils.hasText(user.getLastName()) ? user.getLastName() : cemaUser.getLastName();
        String email = StringUtils.hasText(user.getEmail()) ? user.getEmail() : cemaUser.getEmail();
        String phone = StringUtils.hasText(user.getPhone()) ? user.getPhone() : cemaUser.getPhone();
        Boolean enabled = user.getEnabled() != null ? user.getEnabled() : cemaUser.getEnabled();

        cemaUser.setName(name);
        cemaUser.setLastName(lastName);
        cemaUser.setEmail(email);
        cemaUser.setPhone(phone);
        cemaUser.setEnabled(enabled);

        return cemaUser;
    }
}
