package com.helpie.backend.service;

import com.helpie.backend.domain.basiclogin.BasicLogin;
import com.helpie.backend.domain.basiclogin.LockStatus;
import com.helpie.backend.domain.user.User;
import com.helpie.backend.repository.basiclogin.BasicLoginRepository;
import com.helpie.backend.service.user.UserCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicLoginService {
    private final BasicLoginRepository basicLoginRepository;
    private final UserCommonService userCommonService;

    @Transactional
    public void initialize(Long userId) {
        User user = userCommonService.findById(userId);

        BasicLogin basicLogin = BasicLogin.builder()
                .user(user)
                .lockStatus(LockStatus.UNLOCK)
                .loginFailCount(0)
                .lockedAt(null)
                .build();

        basicLoginRepository.save(basicLogin);
    }

    @Transactional
    public void increaseLoginFailCount(long userId) {
        basicLoginRepository.increaseLoginFailCount(userId);
    }

    @Transactional
    public void resetLoginFailInfo(long userId){
        basicLoginRepository.resetLoginFailInfo(userId);
    }

    @Transactional
    public void lockUser(long userId) {
        basicLoginRepository.lockUser(userId, LocalDateTime.now());
    }

    public BasicLogin findBasicLogin(long userId) {
        return basicLoginRepository.findByUserId(userId);
    }
}
