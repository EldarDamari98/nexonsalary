package com.nexonsalary.service;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.service.handler.*;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommissionCalculationService {

    private final CommissionHandler newClientHandler = new NewClientHandler();
    private final CommissionHandler existingClientHandler = new ExistingClientHandler();
    private final CommissionHandler agentTransferHandler = new AgentTransferHandler(newClientHandler);
    private final CommissionHandler clientLeftHandler = new ClientLeftHandler();

    public void deleteForMonth(LocalDate month) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            LocalDate lastDay = YearMonth.from(month).atEndOfMonth();
            session.createMutationQuery("delete from CommissionTransaction where balanceDate between :firstDay and :lastDay")
                    .setParameter("firstDay", month).setParameter("lastDay", lastDay).executeUpdate();
            session.createMutationQuery(
                    "update ClientAgentHistory set status = 'ACTIVE', leaveDate = null where leaveDate between :firstDay and :lastDay")
                    .setParameter("firstDay", month).setParameter("lastDay", lastDay).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public CommissionCalculationResultDto calculateForMonth(LocalDate month) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            if (hasTransactionsForMonth(session, month)) {
                throw new IllegalStateException("Commission already calculated for month: " + month + ". Delete existing transactions first.");
            }

            List<MonthlyMemberBalance> currentBalances = getBalancesForMonth(session, month);
            if (currentBalances.isEmpty()) {
                throw new IllegalArgumentException("No balance data found for month: " + month);
            }

            CommissionCalculationResultDto result = new CommissionCalculationResultDto();
            Set<Long> processedAccountIds = new HashSet<>();

            for (MonthlyMemberBalance current : currentBalances) {
                Long accountId = current.getAccount().getId();
                MonthlyMemberBalance previous = getPreviousBalance(session, accountId, month);

                CommissionHandler handler = resolveHandler(previous, current);
                handler.handle(session, current, previous, month, result);

                processedAccountIds.add(accountId);
            }

            // Accounts that were ACTIVE but did not appear this month → LEFT
            List<ClientAgentHistory> activeHistories = getActiveHistories(session);
            for (ClientAgentHistory history : activeHistories) {
                Long accountId = history.getAccount().getId();
                if (!processedAccountIds.contains(accountId)) {
                    MonthlyMemberBalance lastBalance = getPreviousBalance(session, accountId, month);
                    if (lastBalance != null) {
                        clientLeftHandler.handle(session, null, lastBalance, month, result);
                    }
                }
            }

            result.setNetCommission(
                    result.getTotalPerimeterFeeNew()
                            .add(result.getTotalPerimeterFeeDelta())
                            .add(result.getTotalTrailCommission())
                            .subtract(result.getTotalClawbacks())
            );
            result.setMessage("Commission calculated successfully for " + month);

            tx.commit();
            return result;

        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private CommissionHandler resolveHandler(MonthlyMemberBalance previous, MonthlyMemberBalance current) {
        if (previous == null) return newClientHandler;
        if (previous.getAgent().getId().equals(current.getAgent().getId())) return existingClientHandler;
        return agentTransferHandler;
    }

    // --- Queries ---

    private boolean hasTransactionsForMonth(Session session, LocalDate firstDay) {
        LocalDate lastDay = YearMonth.from(firstDay).atEndOfMonth();
        Long count = session.createQuery(
                "select count(ct) from CommissionTransaction ct where ct.balanceDate between :firstDay and :lastDay", Long.class
        ).setParameter("firstDay", firstDay).setParameter("lastDay", lastDay).uniqueResult();
        return count != null && count > 0;
    }

    private List<MonthlyMemberBalance> getBalancesForMonth(Session session, LocalDate firstDay) {
        LocalDate lastDay = YearMonth.from(firstDay).atEndOfMonth();
        return session.createQuery(
                "from MonthlyMemberBalance mmb join fetch mmb.member join fetch mmb.agent join fetch mmb.account " +
                "where mmb.balanceDate between :firstDay and :lastDay",
                MonthlyMemberBalance.class
        ).setParameter("firstDay", firstDay).setParameter("lastDay", lastDay).list();
    }

    private MonthlyMemberBalance getPreviousBalance(Session session, Long accountId, LocalDate firstDay) {
        return session.createQuery(
                "from MonthlyMemberBalance mmb join fetch mmb.member join fetch mmb.agent join fetch mmb.account " +
                "where mmb.account.id = :accountId and mmb.balanceDate < :firstDay order by mmb.balanceDate desc",
                MonthlyMemberBalance.class
        ).setParameter("accountId", accountId).setParameter("firstDay", firstDay).setMaxResults(1).uniqueResult();
    }

    private List<ClientAgentHistory> getActiveHistories(Session session) {
        return session.createQuery(
                "from ClientAgentHistory cah join fetch cah.account join fetch cah.agent join fetch cah.member where cah.status = :status",
                ClientAgentHistory.class
        ).setParameter("status", ClientStatus.ACTIVE).list();
    }
}
