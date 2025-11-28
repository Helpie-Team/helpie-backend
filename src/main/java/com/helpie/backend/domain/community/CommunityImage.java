package com.helpie.backend.domain.community;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 첨부 이미지 엔티티
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Entity
@Table(name = "community_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    @Setter
    private Community community;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public CommunityImage(Community community, String imageUrl, String originalFilename, Long fileSize, Integer displayOrder) {
        this.community = community;
        this.imageUrl = imageUrl;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
    }
}