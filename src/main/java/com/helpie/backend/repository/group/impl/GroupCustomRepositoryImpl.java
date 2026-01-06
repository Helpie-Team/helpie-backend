package com.helpie.backend.repository.group.impl;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.group.QBookmark;
import com.helpie.backend.domain.group.QGroup;
import com.helpie.backend.domain.group.QGroupImage;
import com.helpie.backend.domain.group.QGroupMember;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.CursorRequest;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.repository.group.GroupCustomRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupCustomRepositoryImpl implements GroupCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QGroup groupQ = QGroup.group;
    private final QGroupMember groupMemberQ = QGroupMember.groupMember;
    private final QGroupImage img = QGroupImage.groupImage;
    private final QBookmark bookmark = QBookmark.bookmark;

    @Override
    public Page<MyGroupResponse> findMyGroups(Long userId, String status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (status.equals("created")) {
            // 내가 만든 소모임만 조회
            builder.and(groupQ.createdBy.eq(userId));
        } else {
            // 내가 가입한 소모임 조회
            builder.and(groupMemberQ.userId.eq(userId));
        }
        
        if (status.equals("UPCOMING")) {
            builder.and(groupMemberQ.isActive.isTrue())
                    .and(groupMemberQ.leftAt.isNull())
                    .and(groupQ.status.in(GroupStatus.RECRUITING, GroupStatus.RECRUITMENT_CLOSED));
        } else if (status.equals("PAST")) {
            LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
            LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
            builder.and(groupMemberQ.isActive.isFalse())
                    .and(groupQ.status.eq(GroupStatus.COMPLETED))
                    .and(groupQ.meetingDate.goe(start))
                    .and(groupQ.meetingDate.lt(end));
        }

        // Group 엔티티 전체를 가져와서 썸네일 정보 포함 (N+1 문제 해결)
        JPQLQuery<Group> query;
        if (status.equals("created")) {
            // 내가 만든 소모임은 Group에서 직접 조회
            query =queryFactory.selectFrom(groupQ)
                    .leftJoin(groupQ.city).fetchJoin()
                    .leftJoin(groupQ.city.country).fetchJoin()
                    .leftJoin(groupQ.images).fetchJoin()
                    .where(builder)
                    .select(groupQ)
                    .orderBy(groupQ.meetingDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        } else {
            // 가입한 소모임은 GroupMember를 통해 조회
            query = queryFactory.selectFrom(groupQ)
                    .join(groupMemberQ.group, groupQ)
                    .leftJoin(groupQ.city).fetchJoin()
                    .leftJoin(groupQ.city.country).fetchJoin()
                    .leftJoin(groupQ.images).fetchJoin()
                    .where(builder)
                    .select(groupQ)
                    .orderBy(groupQ.meetingDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        }

        List<Group> groups = query.fetch();
        
        // Group 엔티티를 MyGroupResponse로 변환
        List<MyGroupResponse> content = groups.stream()
                .map(group -> new MyGroupResponse(
                        group.getId(),
                        group.getTitle(),
                        group.getDescription(),
                        group.getCity().getName(),
                        group.getCurrentMembers(),
                        group.getMaxMembers(),
                        group.getCategory(),
                        group.getMeetingDate(),
                        group.getThumbnail()
                ))
                .collect(Collectors.toList());

        Long total;
        if (status.equals("created")) {
            total = queryFactory.selectFrom(groupQ)
                    .where(builder)
                    .select(groupQ.count())
                    .fetchOne();
        } else {
            total = queryFactory.selectFrom(groupQ)
                    .join(groupMemberQ.group, groupQ)
                    .where(builder)
                    .select(groupMemberQ.count())
                    .fetchOne();
        }

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    public List<Long> findPageIds(List<City> cities, CursorRequest request) {
        return queryFactory
            .select(groupQ.id)
            .from(groupQ)
            .where(
                filterGroups(groupQ, cities, request.getCategory()),
                cursorCondition(groupQ, request.getCursorCreatedAt(), request.getCursorId())
            )
            .orderBy(groupQ.createdAt.desc(), groupQ.id.desc())
            .limit(request.getSize()+1)
            .fetch();
    }

    public List<GroupResponse> findGroupsWithImagesByIds(List<Long> groupIds,Long userId) {
        return queryFactory
            .select(Projections.constructor(
                        GroupResponse.class,
                        groupQ.id,
                        groupQ.title,
                        groupQ.description,
                        groupQ.city.name,
                        groupQ.category,
                        groupQ.maxMembers,
                        getFirstImage(),
                        Expressions.constant(false),
                        Expressions.constant(3),
                        groupQ.status,
                        groupQ.meetingDate,
                        getBookmarkYn(userId),
                        groupQ.createdAt))
            .from(groupQ)
            .where(groupQ.id.in(groupIds))
            .orderBy(groupQ.createdAt.desc(), groupQ.id.desc())
            .fetch();
    }

    private Expression<Boolean> getBookmarkYn(Long userId) {
        return JPAExpressions
            .select(bookmark.bookmarkYn)
            .from(bookmark)
            .where(bookmark.groupId.eq(groupQ.id)
                .and(bookmark.userId.eq(userId)))
            .exists();
    }


    private Expression<String> getFirstImage() {
        return JPAExpressions
            .select(img.imageUrl)
            .from(img)
            .where(
                img.id.eq(
                    JPAExpressions
                        .select(img.id.min())
                        .from(img)
                        .where(img.group.id.eq(groupQ.id))
                )
            );

    }


    private BooleanExpression filterGroups(QGroup g, List<City> cities, Category category) {
        return g.city.in(cities)
            .and(g.category.eq(category))
            .and(g.status.in(GroupStatus.RECRUITING, GroupStatus.RECRUITMENT_CLOSED));
    }

    private BooleanExpression cursorCondition(QGroup g, LocalDateTime cursorCreatedAt, Long cursorId) {
        if (cursorCreatedAt == null || cursorId == null) return null;

        return g.createdAt.lt(cursorCreatedAt)
            .or(g.createdAt.eq(cursorCreatedAt).and(g.id.lt(cursorId)));
    }




}
