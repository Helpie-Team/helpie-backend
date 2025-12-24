package com.helpie.backend.common.fixtures;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.location.Country;

public class CityFixtures {
    public static final String CODE = "TEST_CITY";

    public static City CITY(Country country) {
        return new City(CODE,"test","test", country, false, 1);
    }
}
