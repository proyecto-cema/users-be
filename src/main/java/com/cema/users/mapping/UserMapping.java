package com.cema.users.mapping;

import com.cema.users.domain.User;
import com.cema.users.entities.CemaUser;

public interface UserMapping {
    User mapEntityToDomain(CemaUser cemaUser);

    CemaUser mapDomainToEntity(User user, String userName, String password);
}
