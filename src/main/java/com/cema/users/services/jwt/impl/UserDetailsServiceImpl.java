package com.cema.users.services.jwt.impl;

import com.cema.users.domain.CemaUserDetails;
import com.cema.users.entities.CemaUser;
import com.cema.users.exceptions.UserDisabledException;
import com.cema.users.exceptions.ValidationException;
import com.cema.users.repositories.CemaUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CemaUserRepository cemaUserRepository;

    public UserDetailsServiceImpl(CemaUserRepository cemaUserRepository) {
        this.cemaUserRepository = cemaUserRepository;
    }

    @Override
    public CemaUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CemaUser user = cemaUserRepository.findCemaUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        if(!user.getEnabled()){
            throw new UserDisabledException("User not enabled");
        }
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().toUpperCase());

        return new CemaUserDetails(user.getUserName(), user.getPassword(), Collections.singletonList(grantedAuthority), user.getEstablishmentCuig());
    }
}