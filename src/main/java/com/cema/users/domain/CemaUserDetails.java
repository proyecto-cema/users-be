package com.cema.users.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CemaUserDetails extends User {

    private String cuig;

    public CemaUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CemaUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public CemaUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String cuig) {
        super(username, password, authorities);
        this.cuig = cuig;
    }

    public CemaUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String cuig) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.cuig = cuig;
    }

    public String getCuig() {
        return cuig;
    }

    public void setCuig(String cuig) {
        this.cuig = cuig;
    }
}
