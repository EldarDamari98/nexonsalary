package com.nexonsalary.service;

import com.nexonsalary.dto.MemberListItemDto;
import com.nexonsalary.model.Member;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MemberQueryService {

    public List<MemberListItemDto> getAllMembers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Member> members = session.createQuery("""
                    from Member m
                    order by m.fullName asc
                    """, Member.class).getResultList();

            List<MemberListItemDto> result = new ArrayList<>();

            for (Member member : members) {
                Long accountsCount = session.createQuery("""
                        select count(a.id)
                        from MemberAccount a
                        where a.member.id = :memberId
                        """, Long.class)
                        .setParameter("memberId", member.getId())
                        .uniqueResult();

                MonthlyMemberBalance latestBalance = session.createQuery("""
                        from MonthlyMemberBalance b
                        where b.member.id = :memberId
                        order by b.balanceDate desc, b.id desc
                        """, MonthlyMemberBalance.class)
                        .setParameter("memberId", member.getId())
                        .setMaxResults(1)
                        .uniqueResult();

                result.add(new MemberListItemDto(
                        member.getId(),
                        member.getFullName(),
                        member.getNationalId(),
                        accountsCount != null ? accountsCount : 0,
                        latestBalance != null ? latestBalance.getTotalBalance() : BigDecimal.ZERO,
                        latestBalance != null && latestBalance.getAgent() != null
                                ? latestBalance.getAgent().getAgentName()
                                : "",
                        latestBalance != null && latestBalance.getBalanceDate() != null
                                ? latestBalance.getBalanceDate().toString()
                                : ""
                ));
            }

            return result;
        }
    }
}