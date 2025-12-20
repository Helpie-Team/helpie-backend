package com.helpie.backend.common.fixtures;

import java.util.List;

public class OsivPatternFixtures {
    public static List<String> excludePatterns() {
        return List.of(
            "/api/v1/group/create",
            "/api/v1/auth/**"
        );
    }

    public static String excludedUri_exact() {
        return "/api/v1/group/create";
    }

    public static String excludedUri_antPattern() {
        return "/api/v1/auth/login";
    }

    public static String includeUri() {
        return "/api/v1/groups";
    }

    public static String containsTrapUri() {
        return "/api/v1/authentication";
    }
}
