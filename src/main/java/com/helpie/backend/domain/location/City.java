package com.helpie.backend.domain.location;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 도시 엔티티
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
@Entity
@Table(name = "cities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code; // SEOUL, NEW_YORK, LONDON, etc.

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 서울, 뉴욕, 런던, etc.

    @Column(name = "english_name", nullable = false, length = 50)
    private String englishName; // Seoul, New York, London, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false; // 즐겨찾는 도시 여부

    @Column(name = "display_order")
    private Integer displayOrder; // 표시 순서

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public City(String code, String name, String englishName, Country country, Boolean isFavorite, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.englishName = englishName;
        this.country = country;
        this.isFavorite = isFavorite != null ? isFavorite : false;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 즐겨찾는 도시로 설정
     */
    public void markAsFavorite() {
        this.isFavorite = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 즐겨찾는 도시에서 제외
     */
    public void unmarkAsFavorite() {
        this.isFavorite = false;
        this.updatedAt = LocalDateTime.now();
    }
}