package com.helpie.backend.domain.sociallogin;

import com.helpie.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Table(
        name = "social_logins",
        uniqueConstraints = {
            @UniqueConstraint(
                columnNames = {"code", "social_type"}
            ),
            @UniqueConstraint(
                columnNames = {"member_id", "social_type"}
            ),
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 128)
    private String code;

    @Column(name = "social_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "raw_data", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> rawData = new HashMap<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private User user;

    public SocialLogin(
            String code,
            SocialType socialType,
            Map<String, Object> rawData,
            User user
    ) {
        this.code = code;
        this.socialType = socialType;
        this.rawData = rawData;
        this.user = user;
    }
}
