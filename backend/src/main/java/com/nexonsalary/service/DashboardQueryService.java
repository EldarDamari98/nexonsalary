package com.nexonsalary.service;

import com.nexonsalary.dto.DashboardSummaryDto;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;

public class DashboardQueryService {

    public DashboardSummaryDto getSummary() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long totalAgents = session.createQuery("""
                    select count(a.id)
                    from Agent a
                    """, Long.class).uniqueResult();

            Long totalMembers = session.createQuery("""
                    select count(m.id)
                    from Member m
                    """, Long.class).uniqueResult();

            Long totalAccounts = session.createQuery("""
                    select count(a.id)
                    from MemberAccount a
                    """, Long.class).uniqueResult();

            BigDecimal totalAssets = session.createQuery("""
                    select coalesce(sum(b.totalBalance), 0)
                    from MonthlyMemberBalance b
                    """, BigDecimal.class).uniqueResult();

            return new DashboardSummaryDto(
                    totalAgents != null ? totalAgents : 0,
                    totalMembers != null ? totalMembers : 0,
                    totalAccounts != null ? totalAccounts : 0,
                    totalAssets != null ? totalAssets : BigDecimal.ZERO
            );
        }
    }
}