package com.cema.users.services.login.impl;

import com.cema.users.entities.CemaUser;
import com.cema.users.services.login.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean login(String password, CemaUser cemaUser) {
        String realPassword = cemaUser.getPassword();
        String proposedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        return realPassword.equals(proposedPassword);
    }
}
