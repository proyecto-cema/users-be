package com.cema.users.services.validation;

import com.cema.users.domain.User;

public interface UserValidationService {
    void validateUserForUsage(User user);
}
