package com.helpie.backend.domain.user;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserVo extends User {
    private final Long id;
    private final String username;
    private final Collection<UserRole> userRoles;

    public UserVo(
            Long id,
            String username,
            Collection<UserRole> roles
    ) {
        super(id + "", "", roles);

        this.userRoles = roles;
        this.username = username;
        this.id = id;
    }
}
