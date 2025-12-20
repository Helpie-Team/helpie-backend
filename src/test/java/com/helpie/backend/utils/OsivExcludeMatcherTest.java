package com.helpie.backend.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.helpie.backend.fixtures.OsivPatternFixtures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

public class OsivExcludeMatcherTest {

    private OsivExcludeMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new OsivExcludeMatcher(OsivPatternFixtures.excludePatterns());
    }

    @Nested
    @DisplayName("엔드포인트 요청 시 ")
    class MatchCase {

        @Test
        @DisplayName("osiv 필터를 거치지 않는다")
        void shouldSkip() {
            assertThat(matcher.shouldSkip(OsivPatternFixtures.excludedUri_exact())).isTrue();
            assertThat(matcher.shouldSkip(OsivPatternFixtures.excludedUri_antPattern())).isTrue();
        }

        @Test
        @DisplayName("osiv 필터를 거친다")
        void shoudNotSkip() {
            assertThat(matcher.shouldSkip(OsivPatternFixtures.includeUri())).isFalse();
            assertThat(matcher.shouldSkip(OsivPatternFixtures.containsTrapUri())).isFalse();
        }
    }


}
