package com.cema.users.services.validation.impl;

import com.cema.users.domain.User;
import com.cema.users.exceptions.UserDisabledException;
import com.cema.users.exceptions.ValidationException;
import com.cema.users.services.validation.UserValidationService;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {

    @Override
    public void validateUserForUsage(User user) {
        if(!user.getEnabled()){
            throw new UserDisabledException("User is disabled");
        }
    }
}
