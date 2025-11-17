package com.helpie.backend.domain.survey;

import java.util.Arrays;

/**
 * 관심사 및 취미 Enum
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum Interest {
    // 문화 취미
    MOVIE_WATCHING("영화 감상"),
    MUSIC_LISTENING("음악 듣기"),
    DRAMA("드라마"),
    DIARY("일기"),
    OTT_VIEWING("OTT 시청"),
    WRITING("글쓰기"),
    READING("독서"),
    
    // 예술 창작
    BAKING("베이킹"),
    HEALTHY_COOKING("건강식 요리"),
    PHOTOGRAPHY("사진 촬영"),
    DIY_CRAFT("DIY 공예"),
    MUSIC_INSTRUMENT("악기 연주"),
    DANCE("춤/댄스"),
    FASHION_STYLING("패션 스타일링"),
    INTERIOR_DECOR("인테리어/꾸미기"),
    
    // 액티비티 라이프
    EXERCISE("운동"),
    WALKING("산책"),
    CLIMBING("클라이밍"),
    TRAVEL("여행"),
    CAFE_HOPPING("카페 탐방"),
    PET_TIME("반려동물과 시간 보내기"),
    
    // 자기계발 성장
    SELF_DEVELOPMENT("자기계발"),
    JOB_PREPARATION("취업준비"),
    
    // 사회 교류
    EXHIBITION("전시회"),
    MUSEUM("미술관 관람"),
    VOLUNTEER("봉사활동"),
    HOUSE_HUNTING("집구하기"),

    //조회 결과 없음
    NO_RESULT("결과 없음");

    private final String description;

    Interest(String description) {
        this.description = description;
    }

    public static Interest getByDescription(String description) {
        return Arrays.stream(values())
            .filter(i -> i.getDescription().equals(description))
            .findFirst()
            .orElse(NO_RESULT);
    }
    
    public static Interest findByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return NO_RESULT;
        }
        
        String normalizedKeyword = keyword.trim().toLowerCase();
        
        // 정확한 매칭 시도
        Interest exactMatch = Arrays.stream(values())
            .filter(i -> i.getDescription().toLowerCase().equals(normalizedKeyword))
            .findFirst()
            .orElse(null);
            
        if (exactMatch != null && exactMatch != NO_RESULT) {
            return exactMatch;
        }
        
        // 부분 매칭 시도 (공백 제거하여 비교)
        Interest partialMatch = Arrays.stream(values())
            .filter(i -> i != NO_RESULT)
            .filter(i -> {
                String normalizedDesc = i.getDescription().toLowerCase().replaceAll("\\s+", "");
                String normalizedKey = normalizedKeyword.replaceAll("\\s+", "");
                return normalizedDesc.contains(normalizedKey) || normalizedKey.contains(normalizedDesc);
            })
            .findFirst()
            .orElse(null);
            
        return partialMatch != null ? partialMatch : NO_RESULT;
    }

    public String getDescription() {
        return description;
    }
}