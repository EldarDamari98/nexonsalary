package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class AbstractCommissionHandler implements CommissionHandler {

    protected BigDecimal calcPerimeterFee(BigDecimal amount) {
        return amount.multiply(CommissionRates.PERIMETER_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    protected BigDecimal calcTrailCommission(BigDecimal amount) {
        return amount.multiply(CommissionRates.TRAIL_COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    protected void persistTransaction(Session session, MonthlyMemberBalance balance, LocalDate month,
                                      BigDecimal prevBal, BigDecimal currBal, BigDecimal delta,
                                      BigDecimal rate, BigDecimal amount,
                                      CommissionDirection direction, CommissionReason reason) {
        session.persist(buildTransaction(balance, month, prevBal, currBal, delta, rate, amount, direction, reason));
    }

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

    protected ClientAgentHistory findActiveHistory(Session session, Long accountId, Long agentId) {
        return session.createQuery(
                "from ClientAgentHistory cah where cah.account.id = :accountId and cah.agent.id = :agentId and cah.status = :status",
                ClientAgentHistory.class
        ).setParameter("accountId", accountId)
                .setParameter("agentId", agentId)
                .setParameter("status", ClientStatus.ACTIVE)
                .uniqueResult();
    }

    protected void applyLeave(Session session, ClientAgentHistory history,
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

        history.setStatus(ClientStatus.LEFT);
        history.setLeaveDate(month);
        session.merge(history);
    }
}
