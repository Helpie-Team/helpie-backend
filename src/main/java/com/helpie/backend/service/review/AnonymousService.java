package com.helpie.backend.service.review;

import com.helpie.backend.domain.review.Anonymity;
import com.helpie.backend.repository.review.AnonymityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnonymousService {
    private final AnonymityRepository anonymityRepository;

    @Transactional
    public Long nextNumber(){
        Anonymity sequence=anonymityRepository.save(new Anonymity());

        return sequence.getId();
    }

}
