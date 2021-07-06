package com.cema.users.services.login;

import com.cema.users.entities.CemaUser;

public interface LoginService {
    boolean login(String password, CemaUser cemaUser);
}
