package com.helpie.backend.service.group;

import com.helpie.backend.repository.group.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMemberCommonService {
    private final GroupMemberRepository groupMemberRepository;
}
