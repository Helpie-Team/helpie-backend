package com.helpie.backend.utils.storage;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupImage;
import com.helpie.backend.repository.group.GroupImageRepository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class LocalImageStorage implements ImageStorage {

    private final GroupImageRepository groupImageRepository;

    @Override
    public String store(MultipartFile file,Group group) {
        try {
            String url=UUID.randomUUID().toString();

            GroupImage gi = GroupImage.builder()
                .group(group)
                .imageUrl(url)
                .build();

            groupImageRepository.save(gi);

            return url;
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public List<String> storeAll(List<MultipartFile> files, Group group) {
        return files.stream().map(file->store(file,group)).toList();
    }
}
