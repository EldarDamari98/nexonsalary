package com.nexonsalary.service;

import com.nexonsalary.dto.AgentListItemDto;
import com.nexonsalary.model.Agent;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class AgentQueryService {

    public List<AgentListItemDto> getAllAgents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Agent> agents = session.createQuery("""
                    from Agent a
                    order by a.agentName asc
                    """, Agent.class).getResultList();

            List<AgentListItemDto> result = new ArrayList<>();
            LocalDate salaryMonth = YearMonth.now().minusMonths(1).atDay(1);

            for (Agent agent : agents) {
                Long membersCount = session.createQuery("""
                        select count(distinct b.member.id)
                        from MonthlyMemberBalance b
                        where b.agent.id = :agentId
                        """, Long.class)
                        .setParameter("agentId", agent.getId())
                        .uniqueResult();

                Long accountsCount = session.createQuery("""
                        select count(distinct b.account.id)
                        from MonthlyMemberBalance b
                        where b.agent.id = :agentId
                        """, Long.class)
                        .setParameter("agentId", agent.getId())
                        .uniqueResult();

                BigDecimal totalAssets = session.createQuery("""
                        select coalesce(sum(b.totalBalance), 0)
                        from MonthlyMemberBalance b
                        where b.agent.id = :agentId
                        """, BigDecimal.class)
                        .setParameter("agentId", agent.getId())
                        .uniqueResult();

                LocalDate latestBalanceDate = session.createQuery("""
                        select max(b.balanceDate)
                        from MonthlyMemberBalance b
                        where b.agent.id = :agentId
                        """, LocalDate.class)
                        .setParameter("agentId", agent.getId())
                        .uniqueResult();

                BigDecimal currentSalary = session.createQuery("""
                        select coalesce(sum(
                            case
                                when ct.direction = com.nexonsalary.model.CommissionDirection.DEBIT
                                    then -ct.commissionAmount
                                else ct.commissionAmount
                            end
                        ), 0)
                        from CommissionTransaction ct
                        where ct.agent.id = :agentId
                          and ct.balanceDate = :salaryMonth
                        """, BigDecimal.class)
                        .setParameter("agentId", agent.getId())
                        .setParameter("salaryMonth", salaryMonth)
                        .uniqueResult();

                result.add(new AgentListItemDto(
                        agent.getId(),
                        agent.getAgentCode(),
                        agent.getAgentName(),
                        membersCount != null ? membersCount : 0,
                        accountsCount != null ? accountsCount : 0,
                        totalAssets != null ? totalAssets : BigDecimal.ZERO,
                        latestBalanceDate != null ? latestBalanceDate.toString() : "",
                        currentSalary != null ? currentSalary : BigDecimal.ZERO
                ));
            }

            return result;
        }
    }
}