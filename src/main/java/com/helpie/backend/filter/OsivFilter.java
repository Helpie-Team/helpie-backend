package com.helpie.backend.filter;

import com.helpie.backend.utils.OsivExcludeMatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

public class OsivFilter extends OpenEntityManagerInViewFilter {

    private final OsivExcludeMatcher excludeMatcher;

    public OsivFilter(OsivExcludeMatcher excludeMatcher) {
        this.excludeMatcher = excludeMatcher;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludeMatcher.shouldSkip(request.getRequestURI());
    }
}

