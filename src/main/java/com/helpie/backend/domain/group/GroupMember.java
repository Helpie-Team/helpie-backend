package com.helpie.backend.domain.group;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 소모임 멤버 엔티티
 * 소모임과 사용자 간의 참여 관계를 관리합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Entity
@Table(name = "group_members", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "user_name", length = 50)
    private String userName;

    public GroupMember(Group group, Long userId, String userName, Boolean isActive) {
        this.group = group;
        this.userId = userId;
        this.userName = userName;
        this.joinedAt = LocalDateTime.now();
        this.isActive = isActive != null ? isActive : true;
    }

    public GroupMember(Group group, Long userId) {
        this(group, userId, null, true);
    }

    /**
     * 소모임에서 나갑니다.
     */
    public void leave() {
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    /**
     * 소모임에 다시 참여합니다.
     */
    public void rejoin() {
        this.isActive = true;
        this.leftAt = null;
    }

}