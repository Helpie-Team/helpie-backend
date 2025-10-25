package com.helpie.backend.domain.user;

import com.helpie.backend.dto.global.IJwtClaim;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserJwtClaim implements IJwtClaim {
    private Long id;
    private String username;
    private Collection<UserRole> userRoles;
}