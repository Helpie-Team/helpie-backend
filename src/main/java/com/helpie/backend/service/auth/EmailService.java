package com.helpie.backend.service.auth;

import com.helpie.backend.domain.email.AuthType;
import com.helpie.backend.domain.email.EmailAuth;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.auth.EmailAuthRepository;
import com.helpie.backend.repository.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailAuthRepository emailAuthRepository;


    private static final String SENDER_EMAIL = "junobee27@gmail.com";
    private static int authNumber;
    private final UserRepository userRepository;

    public MimeMessage createAuthMail(String mail) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authNumber + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Transactional
    public int sendAuthMail(String mail, AuthType authType) {
        checkEmail(mail, authType);
        MimeMessage message = createAuthMail(mail);
        javaMailSender.send(message);
        emailAuthRepository.save(
                EmailAuth.builder()
                        .email(mail)
                        .authType(authType)
                        .authNumber(authNumber)
                        .expired(false)
                        .build());
        return authNumber;
    }

    public static void createNumber() {
        authNumber = (int) (Math.random() * (90000)) + 100000;
    }

    @Transactional
    public String checkValidAuthByEmail(String mail, AuthType authType, Integer authNumber) {
        EmailAuth emailAuth = emailAuthRepository.findValidAuthByEmail(
                mail, authType, authNumber, LocalDateTime.now()
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.EMAIL_AUTH_INVALID,
                                Map.of(
                                        "email", mail
                                )
                        ){});
        emailAuth.expired();
        return "이메일 인증 성공";
    }

    private void checkEmail(String mail, AuthType authType) {
        if (authType.equals(AuthType.PW_AUTH) && !userRepository.existsByEmail(mail)) {
            throw new BusinessException(ErrorCode.NOT_EXIST_EMAIL, "존재하지 않는 이메일입니다.") {
            };
        }
        if (authType.equals(AuthType.EMAIL_AUTH)) {
            // 기존 EmailAuth 기록이 있으면 삭제 (재인증 허용)
            if (emailAuthRepository.existsByEmailAndAuthType(mail, authType)) {
                emailAuthRepository.deleteByEmailAndAuthType(mail, authType);
            }
        }
    }
}
