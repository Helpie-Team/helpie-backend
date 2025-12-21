package com.helpie.backend.service.group;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.service.file.FileService;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupFacade {
    private final ObjectProvider<GroupFacade> selfProvider;
    private final FileService fileService;
    private final GroupService groupService;
    private final CityRepository cityRepository;

    public GroupCreateResponse createGroup(Long userId, GroupCreateRequest request) {
        City city = selfProvider.getObject().validateCity(request.cityId());

        List<String> urls=new ArrayList<>();
        if (!request.images().isEmpty()) urls=fileService.uploadFiles(request.images());

        return groupService.createGroup(userId, request, urls,city);
    }

    public City validateCity(Long cityId) {
        return cityRepository.findById(cityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + cityId));
    }
}
