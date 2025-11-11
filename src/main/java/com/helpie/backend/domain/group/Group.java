package com.helpie.backend.domain.group;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.Interest;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.exception.GroupException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 소모임 엔티티 도시와 관심사를 기반으로 매칭된 소모임 (정원 5명)
 *
 * @author 전우선
 * @since 2025-10-30(목)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ElementCollection(targetClass = Interest.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "group_interests", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "interest")
    @Builder.Default
    private Set<Interest> interests = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GroupImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<GroupMember> members = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private GroupStatus status=GroupStatus.RECRUITING;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "current_members", nullable = false)
    @Builder.Default
    private Integer currentMembers=0;

    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime meetingDate;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소모임에 멤버를 추가합니다.
     */
    public boolean addMember(Long userId) {
        this.currentMembers++;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 소모임에서 멤버를 제거합니다.
     */
    public void removeMember(Long userId) {
        this.currentMembers--;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 소모임이 가득 찼는지 확인합니다. (현재는 무제한이므로 항상 false)
     */
    public boolean isFull() {
        return false;
    }


    public boolean isPopular() {
        return maxMembers >= 6 || currentMembers >= maxMembers / 2;
    }

    public int getDayBefore(){
        LocalDate today = LocalDate.now();
        LocalDate meetingLocalDate = this.meetingDate.toLocalDate();

        if (meetingLocalDate.isBefore(today)) {
            throw new GroupException(ErrorCode.GROUP_ENDED);
        }


        return (int) ChronoUnit.DAYS.between(today, meetingLocalDate);
    }

    public void addImage(GroupImage image) {
        images.add(image);
        image.setGroup(this);
    }

    public String getThumbnail() {
        if (images.isEmpty()) return "NO_IMAGE";
        return this.getImages().get(0).getImageUrl();
    }
}