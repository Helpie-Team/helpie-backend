package com.helpie.backend.dto.review;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewResponse {
    private Long reviewId;
    private String reviewerName;
    private String groupTitle;
    private Integer rate;
    private String description;
    private LocalDateTime meetingDate;
    private List<String> reviewImages;
    private String profileImage;



}
