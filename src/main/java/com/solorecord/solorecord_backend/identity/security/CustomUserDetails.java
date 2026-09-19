package com.solorecord.solorecord_backend.identity.security;

import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.data.model.User;
import lombok.AllArgsConstructor;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private  final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));    }

    @Override
    public  String getPassword() {
        return user.getPasswordHash();
    }


    @Override
    public boolean isEnabled() {
        return user.getStatus() == AccountStatus.ACTIVE;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
