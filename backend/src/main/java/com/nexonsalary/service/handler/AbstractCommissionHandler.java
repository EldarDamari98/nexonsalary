package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Shared helper methods used by all commission handler classes.
 *
 * The four concrete handlers (NewClientHandler, ExistingClientHandler, etc.)
 * all need the same utility operations: calculating fees, building transaction records,
 * finding history records, and applying clawbacks. Rather than copying this code
 * into each handler, we put it here once and let all handlers inherit it.
 *
 * This class implements CommissionHandler but does not override the handle() method —
 * that's left to each concrete subclass.
 */
public abstract class AbstractCommissionHandler implements CommissionHandler {

    /**
     * Calculates the perimeter fee for a given balance amount.
     * Formula: amount × 0.3% (PERIMETER_FEE_RATE), rounded to 2 decimal places.
     *
     * @param amount the account balance to calculate the fee on
     * @return the perimeter fee in ILS
     */
    protected BigDecimal calcPerimeterFee(BigDecimal amount) {
        return amount.multiply(CommissionRates.PERIMETER_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the trail commission for a given balance amount.
     * Formula: amount × 0.025% (TRAIL_COMMISSION_RATE), rounded to 2 decimal places.
     *
     * @param amount the current account balance
     * @return the trail commission in ILS
     */
    protected BigDecimal calcTrailCommission(BigDecimal amount) {
        return amount.multiply(CommissionRates.TRAIL_COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Builds a CommissionTransaction object and immediately saves it to the database.
     *
     * @param session   the active Hibernate session
     * @param balance   the monthly balance record this transaction is based on
     * @param month     the month this transaction belongs to
     * @param prevBal   the account balance last month
     * @param currBal   the account balance this month
     * @param delta     the change in balance (currBal - prevBal)
     * @param rate      the commission rate applied
     * @param amount    the final commission amount in ILS
     * @param direction CREDIT (agent earns money) or DEBIT (agent owes money back)
     * @param reason    why this commission was generated (e.g. TRAIL_COMMISSION, PERIMETER_FEE_NEW)
     */
    protected void persistTransaction(Session session, MonthlyMemberBalance balance, LocalDate month,
                                      BigDecimal prevBal, BigDecimal currBal, BigDecimal delta,
                                      BigDecimal rate, BigDecimal amount,
                                      CommissionDirection direction, CommissionReason reason) {
        session.persist(buildTransaction(balance, month, prevBal, currBal, delta, rate, amount, direction, reason));
    }

    /**
     * Builds a CommissionTransaction object without saving it to the database.
     * Used internally by persistTransaction and by applyLeave for clawback records.
     *
     * @param balance   the monthly balance record this transaction is based on
     * @param month     the month this transaction belongs to
     * @param prevBal   the account balance last month
     * @param currBal   the account balance this month
     * @param delta     the change in balance
     * @param rate      the commission rate applied
     * @param amount    the final commission amount in ILS
     * @param direction CREDIT or DEBIT
     * @param reason    the business reason for this transaction
     * @return the populated (unsaved) CommissionTransaction object
     */
    protected CommissionTransaction buildTransaction(MonthlyMemberBalance balance, LocalDate month,
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

    /**
     * Looks up the active history record for a given account and agent.
     * Returns null if no active history is found (the account is not currently with this agent).
     *
     * @param session   the active Hibernate session
     * @param accountId the ID of the account to look up
     * @param agentId   the ID of the agent to check
     * @return the active ClientAgentHistory, or null if not found
     */
    protected ClientAgentHistory findActiveHistory(Session session, Long accountId, Long agentId) {
        return session.createQuery(
                "from ClientAgentHistory cah where cah.account.id = :accountId and cah.agent.id = :agentId and cah.status = :status",
                ClientAgentHistory.class
        ).setParameter("accountId", accountId)
                .setParameter("agentId", agentId)
                .setParameter("status", ClientStatus.ACTIVE)
                .uniqueResult();
    }

    /**
     * Marks a client as LEFT and applies a clawback deduction if their tenure was short.
     *
     * Clawback rules:
     *   - Less than 12 months with the agent → agent returns 50% of perimeter fees earned
     *   - Between 12 and 24 months           → agent returns 25% of perimeter fees earned
     *   - More than 24 months                → no clawback, agent keeps everything
     *
     * @param session     the active Hibernate session
     * @param history     the history record for this client-agent relationship
     * @param lastBalance the client's last known balance (used as the basis for the clawback transaction)
     * @param month       the month the client left
     * @param result      the running totals object to record the clawback amount in
     */
    protected void applyLeave(Session session, ClientAgentHistory history,
                              MonthlyMemberBalance lastBalance, LocalDate month,
                              CommissionCalculationResultDto result) {
        // Calculate how many full months the client was with this agent
        long tenureMonths = ChronoUnit.MONTHS.between(history.getFirstAppearanceDate(), month);

        BigDecimal clawbackRate = BigDecimal.ZERO;
        if (tenureMonths < 12) {
            clawbackRate = CommissionRates.CLAWBACK_UNDER_12_MONTHS;
        } else if (tenureMonths < 24) {
            clawbackRate = CommissionRates.CLAWBACK_12_TO_24_MONTHS;
        }

        // Only create a clawback transaction if there's actually something to take back
        if (clawbackRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal clawbackAmount = history.getTotalPerimeterFeePaid()
                    .multiply(clawbackRate)
                    .setScale(2, RoundingMode.HALF_UP);

            CommissionTransaction clawbackTx = buildTransaction(lastBalance, month,
                    lastBalance.getTotalBalance(), BigDecimal.ZERO,
                    lastBalance.getTotalBalance().negate(),
                    clawbackRate, clawbackAmount,
                    CommissionDirection.DEBIT, CommissionReason.PERIMETER_FEE_CLAWBACK);
            session.persist(clawbackTx);

            result.setTotalClawbacks(result.getTotalClawbacks().add(clawbackAmount));
            result.setTransactionCount(result.getTransactionCount() + 1);
        }

        // Update the history record to reflect the client has left
        history.setStatus(ClientStatus.LEFT);
        history.setLeaveDate(month);
        session.merge(history);
    }
}
