package com.helpie.backend.repository.group.impl;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.group.QGroup;
import com.helpie.backend.domain.group.QGroupMember;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.repository.group.GroupCustomRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    public List<Group> findPage(List<City> cities, Category category, LocalDateTime cursorCreatedAt, Long cursorId, int size) {

        return queryFactory
            .selectFrom(groupQ)
            .leftJoin(groupQ.images).fetchJoin()
            .where(
                filterGroups(groupQ,cities,category),
                cursorCondition(groupQ,cursorCreatedAt,cursorId)
            )
            .orderBy(groupQ.createdAt.desc(), groupQ.id.desc())
            .limit(size)
            .fetch();
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
