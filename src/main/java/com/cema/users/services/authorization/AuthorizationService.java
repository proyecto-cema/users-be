package com.cema.users.services.authorization;

import org.springframework.security.core.Authentication;

public interface AuthorizationService {
    String getCurrentUserCuig();

    boolean isOnTheSameEstablishment(String cuig);

    boolean isAdmin();
}
