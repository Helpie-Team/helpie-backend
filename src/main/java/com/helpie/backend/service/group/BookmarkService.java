package com.helpie.backend.service.group;

import com.helpie.backend.domain.group.Bookmark;
import com.helpie.backend.dto.group.BookmarkResponse;
import com.helpie.backend.dto.group.BookmarkStatus;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.repository.bookmark.BookmarkCustomRepository;
import com.helpie.backend.repository.group.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkCustomRepository bookmarkCustomRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
    관심 소모임 조회
     */
    public Page<GroupResponse> getByPreference(Long userId, Pageable pageable) {
            return bookmarkCustomRepository.findLikeGroups(userId, pageable)
                .map(GroupResponse::from);
    }

    @Transactional
    public BookmarkResponse toggleBookmark(Long userId,Long groupId){
        if (!bookmarkRepository.existsByUserIdAndGroupId(userId,groupId)){
            bookmarkRepository.save(new Bookmark(userId,groupId));
            return new BookmarkResponse(BookmarkStatus.ADDED);
        }

        Bookmark bookmark=bookmarkRepository.findByUserIdAndGroupId(userId,groupId);
        boolean liked = bookmark.update();

        if (liked) {
            return new BookmarkResponse(BookmarkStatus.ADDED);
        }

        return new BookmarkResponse(BookmarkStatus.REMOVED);

    }

}
