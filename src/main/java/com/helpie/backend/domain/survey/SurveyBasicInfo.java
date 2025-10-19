package com.helpie.backend.domain.survey;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 설문조사 기본정보 엔티티
 * 사용자의 기본 프로필 정보(나라, 성별, 나이대, 언어, 관심사)를 관리합니다.
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Entity
@Table(name = "survey_basic_infos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyBasicInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private Country country;

    /** 복수 선택 가능한 사용 언어 목록 */
    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "survey_languages", joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "language")
    private Set<Language> languages;

    /** 복수 선택 가능한 관심사 목록 */
    @ElementCollection(targetClass = Interest.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "survey_interests", joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "interest")
    private Set<Interest> interests;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SurveyBasicInfo(Long userId, Country country, Gender gender, AgeGroup ageGroup, Set<Language> languages, Set<Interest> interests) {
        this.userId = userId;
        this.country = country;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.languages = languages;
        this.interests = interests;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 기본정보를 새로운 값으로 업데이트합니다.
     */
    public void updateBasicInfo(Country country, Gender gender, AgeGroup ageGroup, Set<Language> languages, Set<Interest> interests) {
        this.country = country;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.languages = languages;
        this.interests = interests;
        this.updatedAt = LocalDateTime.now();
    }
}