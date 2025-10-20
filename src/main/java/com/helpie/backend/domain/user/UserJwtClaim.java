package com.helpie.backend.domain.user;

import com.helpie.backend.dto.global.IJwtClaim;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJwtClaim implements IJwtClaim {
    private Long id;
    private String username;
    private Collection<UserRole> userRoles;

    public UserJwtClaim(
            Long id,
            String username,
            Collection<UserRole> memberRoles
    ) {
        this.id = id;
        this.username = username;
        this.userRoles = userRoles;
    }
}