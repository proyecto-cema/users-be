package com.cema.users.services.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
    String getUsernameFromToken(String token);

    String generateToken(UserDetails userDetails);

    Boolean validateToken(String token, UserDetails userDetails);
}
