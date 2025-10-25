package com.helpie.backend.domain.group;

import com.helpie.backend.domain.survey.Country;
import com.helpie.backend.domain.survey.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 소모임 엔티티
 * 나라와 관심사를 기반으로 매칭된 소모임 (정원 5명)
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Entity
@Table(name = "user_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private Country country;

    @ElementCollection(targetClass = Interest.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "group_interests", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "interest")
    @Builder.Default
    private Set<Interest> interests = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<GroupMember> members = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupStatus status;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "current_members", nullable = false)
    private Integer currentMembers;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    public Group(String title, String description, Country country, Set<Interest> interests, Integer maxMembers, Integer currentMembers, GroupStatus status, Long createdBy) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.interests = interests != null ? new HashSet<>(interests) : new HashSet<>();
        this.status = status != null ? status : GroupStatus.ACTIVE;
        this.maxMembers = maxMembers != null ? maxMembers : 5;
        this.currentMembers = currentMembers != null ? currentMembers : 0;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Group(String title, String description, Country country, Set<Interest> interests) {
        this(title, description, country, interests, 5, 0, GroupStatus.ACTIVE, null);
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소모임에 멤버를 추가합니다.
     */
    public boolean addMember(Long userId) {
        if (this.currentMembers >= this.maxMembers) {
            return false;
        }
        
        this.currentMembers++;
        if (this.currentMembers >= this.maxMembers) {
            this.status = GroupStatus.FULL;
        }
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 소모임에서 멤버를 제거합니다.
     */
    public void removeMember(Long userId) {
        this.currentMembers--;
        if (this.currentMembers < this.maxMembers && this.status == GroupStatus.FULL) {
            this.status = GroupStatus.ACTIVE;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소모임이 가득 찼는지 확인합니다.
     */
    public boolean isFull() {
        return this.currentMembers >= this.maxMembers;
    }

    /**
     * 소모임이 활성 상태인지 확인합니다.
     */
    public boolean isActive() {
        return this.status == GroupStatus.ACTIVE;
    }
}