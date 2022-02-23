package com.cema.users.services.jwt.impl;


import com.cema.users.entities.CemaUser;
import com.cema.users.repositories.CemaUserRepository;
import org.junit.Rule;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDetailsServiceImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private CemaUserRepository cemaUserRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(cemaUserRepository);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWithEntityCredentials() {
        String userName = "userName";
        String password = "password";
        String role = "role";
        CemaUser user = new CemaUser();
        user.setUserName(userName);
        user.setPassword(password);
        user.setRole(role);
        user.setEnabled(true);

        when(cemaUserRepository.findCemaUserByUserName(userName)).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        assertThat(userDetails.getUsername(), is(userName));
        assertThat(userDetails.getPassword(), is(password));
        Set<GrantedAuthority> grantedAuthorities = (Set<GrantedAuthority>) userDetails.getAuthorities();

        assertThat(grantedAuthorities.iterator().next().getAuthority(), is(role.toUpperCase()));
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenNonExistantUser() {
        String userName = "userName";

        assertThrows("User not found with username: " + userName, UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));
    }

}