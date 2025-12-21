package com.helpie.backend.config;

import com.helpie.backend.filter.OsivFilter;
import com.helpie.backend.utils.OsivExcludeMatcher;
import com.helpie.backend.utils.OsivExcludePaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@Configuration
public class OsivConfig {

    @Bean
    public OsivExcludeMatcher osivExcludeMatcher() {
        return new OsivExcludeMatcher(OsivExcludePaths.EXCLUDE_PATTERNS);
    }

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter(OsivExcludeMatcher matcher) {
        return new OsivFilter(matcher);
    }


}
