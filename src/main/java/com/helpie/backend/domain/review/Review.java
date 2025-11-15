package com.helpie.backend.domain.review;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="rate")
    private Integer rate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name="anonymityYn")
    private Boolean anonymityYn;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    public void addImage(ReviewImage image) {
        images.add(image);
        image.setReview(this);
    }

}
