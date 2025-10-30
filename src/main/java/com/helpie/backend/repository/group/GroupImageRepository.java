package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.GroupImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupImageRepository extends JpaRepository<GroupImage, Integer> {

}
