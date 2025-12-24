package com.helpie.backend.common.builder;

import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuilderSupporter {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CountryRepository countryRepository;

    public CityRepository cityRepository() {
        return cityRepository;
    }

    public GroupRepository groupRepository() {
        return groupRepository;
    }

    public CountryRepository countryRepository() {
        return countryRepository;
    }

}
