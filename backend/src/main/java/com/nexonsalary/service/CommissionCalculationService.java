package com.nexonsalary.service;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CommissionCalculationService {

    public void deleteForMonth(LocalDate month) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.createMutationQuery("delete from CommissionTransaction where balanceDate = :month")
                    .setParameter("month", month).executeUpdate();
            session.createMutationQuery(
                    "update ClientAgentHistory set status = 'ACTIVE', leaveDate = null where leaveDate = :month")
                    .setParameter("month", month).executeUpdate();
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

                if (previous == null) {
                    handleNewClient(session, current, month, result);
                } else {
                    Long currentAgentId = current.getAgent().getId();
                    Long previousAgentId = previous.getAgent().getId();

                    if (currentAgentId.equals(previousAgentId)) {
                        handleExistingClient(session, current, previous, month, result);
                    } else {
                        handleAgentTransfer(session, current, previous, month, result);
                    }
                }

                processedAccountIds.add(accountId);
            }

            // Accounts that were ACTIVE but did not appear this month → LEFT
            List<ClientAgentHistory> activeHistories = getActiveHistories(session);
            for (ClientAgentHistory history : activeHistories) {
                Long accountId = history.getAccount().getId();
                if (!processedAccountIds.contains(accountId)) {
                    MonthlyMemberBalance lastBalance = getPreviousBalance(session, accountId, month);
                    if (lastBalance != null) {
                        handleClientLeft(session, history, lastBalance, month, result);
                    }
                }
            }

            result.setNetCommission(
                    result.getTotalScopeNew()
                            .add(result.getTotalScopeDelta())
                            .add(result.getTotalNifra())
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

    // --- Handlers ---

    private void handleNewClient(Session session, MonthlyMemberBalance current,
                                  LocalDate month, CommissionCalculationResultDto result) {
        BigDecimal scopeAmount = calcScope(current.getTotalBalance());
        BigDecimal nifraAmount = calcNifra(current.getTotalBalance());

        ClientAgentHistory history = new ClientAgentHistory(
                current.getAccount(), current.getAgent(), current.getMember(), month, scopeAmount
        );
        session.persist(history);

        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                current.getTotalBalance(), CommissionRates.SCOPE_RATE, scopeAmount,
                CommissionDirection.CREDIT, CommissionReason.SCOPE_NEW);

        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                BigDecimal.ZERO, CommissionRates.NIFRA_RATE, nifraAmount,
                CommissionDirection.CREDIT, CommissionReason.NIFRA);

        result.setNewClients(result.getNewClients() + 1);
        result.setTotalScopeNew(result.getTotalScopeNew().add(scopeAmount));
        result.setTotalNifra(result.getTotalNifra().add(nifraAmount));
        result.setTransactionCount(result.getTransactionCount() + 2);
    }

    private void handleExistingClient(Session session, MonthlyMemberBalance current,
                                       MonthlyMemberBalance previous, LocalDate month,
                                       CommissionCalculationResultDto result) {
        BigDecimal delta = current.getTotalBalance().subtract(previous.getTotalBalance());

        ClientAgentHistory history = findActiveHistory(session, current.getAccount().getId(), current.getAgent().getId());
        int txCount = 0;

        // Scope only on positive delta (new money brought in)
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal scopeAmount = calcScope(delta);
            if (history != null) {
                history.setTotalScopePaid(history.getTotalScopePaid().add(scopeAmount));
                session.merge(history);
            }
            persistTransaction(session, current, month, previous.getTotalBalance(), current.getTotalBalance(),
                    delta, CommissionRates.SCOPE_RATE, scopeAmount,
                    CommissionDirection.CREDIT, CommissionReason.SCOPE_DELTA);
            result.setTotalScopeDelta(result.getTotalScopeDelta().add(scopeAmount));
            txCount++;
        }

        // Nifra always on current balance
        BigDecimal nifraAmount = calcNifra(current.getTotalBalance());
        persistTransaction(session, current, month, previous.getTotalBalance(), current.getTotalBalance(),
                delta, CommissionRates.NIFRA_RATE, nifraAmount,
                CommissionDirection.CREDIT, CommissionReason.NIFRA);
        result.setTotalNifra(result.getTotalNifra().add(nifraAmount));
        txCount++;

        result.setExistingClients(result.getExistingClients() + 1);
        result.setTransactionCount(result.getTransactionCount() + txCount);
    }

    private void handleAgentTransfer(Session session, MonthlyMemberBalance current,
                                      MonthlyMemberBalance previous, LocalDate month,
                                      CommissionCalculationResultDto result) {
        // Old agent: apply leave rules
        ClientAgentHistory oldHistory = findActiveHistory(session, previous.getAccount().getId(), previous.getAgent().getId());
        if (oldHistory != null) {
            applyLeave(session, oldHistory, previous, month, result);
        }

        // New agent: treat as new client
        handleNewClient(session, current, month, result);

        // Adjust counters: this is a transfer, not purely a new client
        result.setNewClients(result.getNewClients() - 1);
        result.setTransferredClients(result.getTransferredClients() + 1);
    }

    private void handleClientLeft(Session session, ClientAgentHistory history,
                                   MonthlyMemberBalance lastBalance, LocalDate month,
                                   CommissionCalculationResultDto result) {
        applyLeave(session, history, lastBalance, month, result);
        result.setLeftClients(result.getLeftClients() + 1);
    }

    // --- Leave / Clawback logic ---

    private void applyLeave(Session session, ClientAgentHistory history,
                             MonthlyMemberBalance lastBalance, LocalDate month,
                             CommissionCalculationResultDto result) {
        long tenureMonths = ChronoUnit.MONTHS.between(history.getFirstAppearanceDate(), month);

        BigDecimal clawbackRate = BigDecimal.ZERO;
        if (tenureMonths < 12) {
            clawbackRate = CommissionRates.CLAWBACK_UNDER_12_MONTHS;
        } else if (tenureMonths < 24) {
            clawbackRate = CommissionRates.CLAWBACK_12_TO_24_MONTHS;
        }

        if (clawbackRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal clawbackAmount = history.getTotalScopePaid()
                    .multiply(clawbackRate)
                    .setScale(2, RoundingMode.HALF_UP);

            CommissionTransaction clawbackTx = buildTransaction(lastBalance, month,
                    lastBalance.getTotalBalance(), BigDecimal.ZERO,
                    lastBalance.getTotalBalance().negate(),
                    clawbackRate, clawbackAmount,
                    CommissionDirection.DEBIT, CommissionReason.SCOPE_CLAWBACK);
            session.persist(clawbackTx);

            result.setTotalClawbacks(result.getTotalClawbacks().add(clawbackAmount));
            result.setTransactionCount(result.getTransactionCount() + 1);
        }

        history.setStatus(ClientStatus.LEFT);
        history.setLeaveDate(month);
        session.merge(history);
    }

    // --- Helpers ---

    private void persistTransaction(Session session, MonthlyMemberBalance balance, LocalDate month,
                                     BigDecimal prevBal, BigDecimal currBal, BigDecimal delta,
                                     BigDecimal rate, BigDecimal amount,
                                     CommissionDirection direction, CommissionReason reason) {
        session.persist(buildTransaction(balance, month, prevBal, currBal, delta, rate, amount, direction, reason));
    }

    private CommissionTransaction buildTransaction(MonthlyMemberBalance balance, LocalDate month,
                                                    BigDecimal prevBal, BigDecimal currBal, BigDecimal delta,
                                                    BigDecimal rate, BigDecimal amount,
                                                    CommissionDirection direction, CommissionReason reason) {
        CommissionTransaction tx = new CommissionTransaction();
        tx.setMember(balance.getMember());
        tx.setAgent(balance.getAgent());
        tx.setAccount(balance.getAccount());
        tx.setBalanceDate(month);
        tx.setPreviousBalance(prevBal);
        tx.setCurrentBalance(currBal);
        tx.setDeltaBalance(delta);
        tx.setCommissionRate(rate);
        tx.setCommissionAmount(amount);
        tx.setDirection(direction);
        tx.setReason(reason);
        return tx;
    }

    private BigDecimal calcScope(BigDecimal amount) {
        return amount.multiply(CommissionRates.SCOPE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcNifra(BigDecimal amount) {
        return amount.multiply(CommissionRates.NIFRA_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    // --- Queries ---

    private boolean hasTransactionsForMonth(Session session, LocalDate month) {
        Long count = session.createQuery(
                "select count(ct) from CommissionTransaction ct where ct.balanceDate = :month", Long.class
        ).setParameter("month", month).uniqueResult();
        return count != null && count > 0;
    }

    private List<MonthlyMemberBalance> getBalancesForMonth(Session session, LocalDate month) {
        return session.createQuery(
                "from MonthlyMemberBalance mmb join fetch mmb.member join fetch mmb.agent join fetch mmb.account where mmb.balanceDate = :month",
                MonthlyMemberBalance.class
        ).setParameter("month", month).list();
    }

    private MonthlyMemberBalance getPreviousBalance(Session session, Long accountId, LocalDate beforeMonth) {
        return session.createQuery(
                "from MonthlyMemberBalance mmb join fetch mmb.member join fetch mmb.agent join fetch mmb.account " +
                "where mmb.account.id = :accountId and mmb.balanceDate < :month order by mmb.balanceDate desc",
                MonthlyMemberBalance.class
        ).setParameter("accountId", accountId).setParameter("month", beforeMonth).setMaxResults(1).uniqueResult();
    }

    private List<ClientAgentHistory> getActiveHistories(Session session) {
        return session.createQuery(
                "from ClientAgentHistory cah join fetch cah.account join fetch cah.agent join fetch cah.member where cah.status = :status",
                ClientAgentHistory.class
        ).setParameter("status", ClientStatus.ACTIVE).list();
    }

    private ClientAgentHistory findActiveHistory(Session session, Long accountId, Long agentId) {
        return session.createQuery(
                "from ClientAgentHistory cah where cah.account.id = :accountId and cah.agent.id = :agentId and cah.status = :status",
                ClientAgentHistory.class
        ).setParameter("accountId", accountId).setParameter("agentId", agentId)
                .setParameter("status", ClientStatus.ACTIVE).uniqueResult();
    }
}
