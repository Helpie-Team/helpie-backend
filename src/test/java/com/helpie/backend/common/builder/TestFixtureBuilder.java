package com.helpie.backend.common.builder;

import com.helpie.backend.common.fixtures.CityFixtures;
import com.helpie.backend.common.fixtures.CountryFixtures;
import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupImage;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.location.Country;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestFixtureBuilder {

    @Autowired
    private BuilderSupporter bs;

    public Group buildGroup(LocalDateTime createdAt) {
        Group group = Group.builder()
            .title("test")
            .description("description")
            .city(buildCity())
            .category(Category.HOBBY)
            .maxMembers(5)
            .currentMembers(0)
            .meetingDate(LocalDateTime.of(2025, 12, 30, 19, 0))
            .createdAt(createdAt)
            .createdBy(1L)
            .build();
        return bs.groupRepository().save(group);
    }

    public City buildCity() {
        CityRepository repository = bs.cityRepository();
        Country country = buildCountry();

        return repository.findByCodeAndCountry(CityFixtures.CODE, country)
            .orElseGet(() -> repository.save(CityFixtures.CITY(country)));

    }

    public Country buildCountry() {
        CountryRepository repository = bs.countryRepository();

        return repository.findByCode(CountryFixtures.CODE)
            .orElseGet(() -> repository.save(CountryFixtures.COUNTRY()));

    }

    public GroupImage buildGroupImage(Group group,String imageUrl){
        GroupImage image=new GroupImage(group,imageUrl);

        return bs.groupImageRepository().save(image);
    }

}
