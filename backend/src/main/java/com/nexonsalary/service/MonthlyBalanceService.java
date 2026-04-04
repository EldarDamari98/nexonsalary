package com.nexonsalary.service;

import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import com.nexonsalary.model.Agent;
import com.nexonsalary.model.Member;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MonthlyBalanceService {

    public int saveMonthlyBalances(List<MonthlyMemberBalanceDto> balances, String sourceFileName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        int savedCount = 0;

        try {
            for (MonthlyMemberBalanceDto dto : balances) {
                Agent agent = findOrCreateAgent(session, dto.getAgentCode(), dto.getAgentName());
                Member member = findOrCreateMember(session, dto.getNationalId(), dto.getMemberName());

                MonthlyMemberBalance existing = findExistingBalance(
                        session,
                        member.getId(),
                        agent.getId(),
                        dto.getBalanceDate()
                );

                if (existing == null) {
                    MonthlyMemberBalance balance = new MonthlyMemberBalance(
                            member,
                            agent,
                            dto.getBalanceDate(),
                            dto.getTotalBalance(),
                            sourceFileName
                    );

                    session.persist(balance);
                    savedCount++;
                } else {
                    existing.setTotalBalance(dto.getTotalBalance());
                    existing.setSourceFileName(sourceFileName);
                    session.merge(existing);
                }
            }

            transaction.commit();
            return savedCount;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    private Agent findOrCreateAgent(Session session, String agentCode, String agentName) {
        Query<Agent> query = session.createQuery(
                "from Agent where agentCode = :agentCode", Agent.class);
        query.setParameter("agentCode", agentCode);

        Agent agent = query.uniqueResult();

        if (agent == null) {
            agent = new Agent(agentCode, agentName);
            session.persist(agent);
        } else if (agentName != null && !agentName.isBlank()) {
            agent.setAgentName(agentName);
            session.merge(agent);
        }

        return agent;
    }

    private Member findOrCreateMember(Session session, String nationalId, String memberName) {
        Query<Member> query = session.createQuery(
                "from Member where nationalId = :nationalId", Member.class);
        query.setParameter("nationalId", nationalId);

        Member member = query.uniqueResult();

        if (member == null) {
            member = new Member(nationalId, memberName);
            session.persist(member);
        } else if (memberName != null && !memberName.isBlank()) {
            member.setFullName(memberName);
            session.merge(member);
        }

        return member;
    }

    private MonthlyMemberBalance findExistingBalance(Session session,
                                                     Long memberId,
                                                     Long agentId,
                                                     java.time.LocalDate balanceDate) {
        Query<MonthlyMemberBalance> query = session.createQuery("""
                from MonthlyMemberBalance
                where member.id = :memberId
                  and agent.id = :agentId
                  and balanceDate = :balanceDate
                """, MonthlyMemberBalance.class);

        query.setParameter("memberId", memberId);
        query.setParameter("agentId", agentId);
        query.setParameter("balanceDate", balanceDate);

        return query.uniqueResult();
    }
}
