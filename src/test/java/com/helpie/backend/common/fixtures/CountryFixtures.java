package com.helpie.backend.common.fixtures;

import com.helpie.backend.domain.location.Country;

public class CountryFixtures {
    public static final String CODE = "TEST_COUNTRY";

    public static Country COUNTRY() {
        return new Country(CODE, "test", "test");
    }
}
