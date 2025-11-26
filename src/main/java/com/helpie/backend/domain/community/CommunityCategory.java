package com.helpie.backend.domain.community;

/**
 * 커뮤니티 카테고리 enum
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
public enum CommunityCategory {
    ALL("전체"),
    INFO_SHARE("정보공유"),
    FREE_BOARD("자유게시판");
    
    private final String displayName;
    
    CommunityCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}