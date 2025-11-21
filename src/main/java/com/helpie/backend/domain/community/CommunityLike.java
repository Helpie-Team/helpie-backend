package com.helpie.backend.domain.community;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 커뮤니티 좋아요 엔티티
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Entity
@Table(name = "community_likes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public CommunityLike(Community community, Long userId, String username) {
        this.community = community;
        this.userId = userId;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }
}