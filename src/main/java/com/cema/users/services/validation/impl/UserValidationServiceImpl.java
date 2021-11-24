package com.cema.users.services.validation.impl;

import com.cema.users.domain.User;
import com.cema.users.services.validation.UserValidationService;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {

    @Override
    public void validateUserForUsage(User user) {
        //TODO Implement validation with subscription
    }
}
