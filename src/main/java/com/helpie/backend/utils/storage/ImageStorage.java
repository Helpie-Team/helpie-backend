package com.helpie.backend.utils.storage;

import com.helpie.backend.domain.group.Group;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String store(MultipartFile file,Group group);
    List<String> storeAll(List<MultipartFile> files, Group group);
}
