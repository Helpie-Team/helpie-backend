package com.helpie.backend.utils;

import java.util.List;
import org.springframework.util.AntPathMatcher;

public class OsivExcludeMatcher {

    private final List<String> excludePatterns;
    private final AntPathMatcher pathMatcher;

    public OsivExcludeMatcher(List<String> excludePatterns) {
        this(excludePatterns, new AntPathMatcher());
    }

    public OsivExcludeMatcher(List<String> excludePatterns, AntPathMatcher pathMatcher) {
        this.excludePatterns = excludePatterns;
        this.pathMatcher = pathMatcher;
    }

    public boolean shouldSkip(String uri) {
        return excludePatterns.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

}
