package com.helpie.backend.repository.group.impl;

import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.group.QGroup;
import com.helpie.backend.domain.group.QGroupMember;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.repository.group.GroupCustomRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class GroupCustomRepositoryImpl extends QuerydslRepositorySupport implements GroupCustomRepository {
    public GroupCustomRepositoryImpl() {super(GroupMember.class);}

    private final QGroup groupQ = QGroup.group;
    private final QGroupMember groupMemberQ = QGroupMember.groupMember;

    @Override
    public Page<MyGroupResponse> findMyGroups(Long userId, GroupStatus groupStatus, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(groupMemberQ.userId.eq(userId));
        if (groupStatus == GroupStatus.RECRUITING || groupStatus == GroupStatus.RECRUITMENT_CLOSED) {
            builder.and(groupMemberQ.isActive.isTrue())
                    .and(groupQ.status.in(GroupStatus.RECRUITING, GroupStatus.RECRUITMENT_CLOSED));
        } else if (groupStatus == GroupStatus.COMPLETED) {
            LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
            LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
            builder.and(groupMemberQ.isActive.isFalse())
                    .and(groupQ.status.eq(GroupStatus.COMPLETED))
                    .and(groupQ.meetingDate.goe(start))
                    .and(groupQ.meetingDate.lt(end));
        }

        JPQLQuery<MyGroupResponse> query = from(groupMemberQ)
                .join(groupMemberQ.group, groupQ)
                .where(builder)
                .select(Projections.constructor(MyGroupResponse.class,
                        groupQ.id,
                        groupQ.title,
                        groupQ.description,
                        groupQ.city.name,
                        groupQ.currentMembers,
                        groupQ.maxMembers,
                        groupQ.category,
                        groupQ.meetingDate
                ))
                .orderBy(groupQ.meetingDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<MyGroupResponse> content = query.fetch();
        Long total = from(groupMemberQ)
                .join(groupMemberQ.group, groupQ)
                .where(builder)
                .select(groupMemberQ.count())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
