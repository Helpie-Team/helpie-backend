package com.helpie.backend.repository.review;

import com.helpie.backend.domain.review.Anonymity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymityRepository extends JpaRepository<Anonymity, Integer> {

}
