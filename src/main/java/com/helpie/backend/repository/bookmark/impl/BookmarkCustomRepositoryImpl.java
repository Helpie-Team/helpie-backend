package com.helpie.backend.repository.bookmark.impl;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.QBookmark;
import com.helpie.backend.domain.group.QGroup;
import com.helpie.backend.repository.bookmark.BookmarkCustomRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkCustomRepositoryImpl implements BookmarkCustomRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Group> findLikeGroups(Long userId, Pageable pageable) {
            QGroup g = QGroup.group;
            QBookmark b = QBookmark.bookmark;

            List<Group> content = queryFactory
                .select(g)
                .from(b)
                .join(g).on(g.id.eq(b.groupId))
                .where(
                    b.userId.eq(userId),
                    b.bookmarkYn.isTrue()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(g.createdAt.desc())
                .fetch();

            JPAQuery<Long> countQuery = queryFactory
                .select(g.countDistinct())
                .from(b)
                .join(g).on(g.id.eq(b.groupId))
                .where(
                    b.userId.eq(userId),
                    b.bookmarkYn.isTrue()
                );

            return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }
}
