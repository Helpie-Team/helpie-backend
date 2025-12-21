package com.helpie.backend.common.fixtures;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public class AuthFixtures {

    public static UserVo user() {
        return new UserVo(
            1L,
            "user",
            List.of(UserRole.USER)
        );
    }

    public static Authentication authentication() {
        User user=user();

        return new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities()

        );
    }

}
