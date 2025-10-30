package com.helpie.backend.service.group;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.utils.storage.ImageStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ImageStorage imageStorage;

    @Transactional
    public GroupCreateResponse createGroup(Long userId,GroupCreateRequest req, List<MultipartFile> images) {

        Group group = new Group(
            req.title(),
            req.description(),
            req.country(),
            req.category(),
            req.interests(),
            req.maxMember()
        );

        Group saved=groupRepository.save(group);
        groupMemberRepository.save(new GroupMember(saved,userId));
        List<String> urls=imageStorage.storeAll(images,group);

        return new GroupCreateResponse(
            saved.getId(),
            saved.getTitle(),
            saved.getDescription(),
            saved.getMaxMembers(),
            saved.getCountry().name(),
            saved.getInterests(),
            urls
        );

    }


}
