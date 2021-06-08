package com.cema.users.services.login.impl;

import com.cema.users.entities.CemaUser;
import com.cema.users.services.login.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class LoginServiceImplTest {

    private LoginService loginService = new LoginServiceImpl();

    @Test
    public void loginShouldReturnTrueWhenThePasswordIsCorrect(){
        String password = "somePassword";
        CemaUser cemaUser = new CemaUser();
        String hashedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        cemaUser.setPassword(hashedPassword);

        boolean result = loginService.login(password, cemaUser);

        assertThat(result, is(true));
    }

    @Test
    public void loginShouldReturnFalseWhenThePasswordIsIncorrect(){
        String password = "somePassword";
        CemaUser cemaUser = new CemaUser();
        String hashedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        cemaUser.setPassword(hashedPassword);

        boolean result = loginService.login("someOtherPassword", cemaUser);

        assertThat(result, is(false));
    }

}