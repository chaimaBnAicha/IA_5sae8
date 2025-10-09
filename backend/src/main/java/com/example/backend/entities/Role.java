package com.example.backend.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    MANAGER;
    @Override
    public String getAuthority() {
        return name();  // Returns the name of the role as the authority
    }
}
