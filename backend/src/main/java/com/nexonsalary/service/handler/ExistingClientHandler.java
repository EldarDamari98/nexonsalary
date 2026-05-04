package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.CommissionDirection;
import com.nexonsalary.model.CommissionReason;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Handles commission calculation for a returning client who is still with the same agent.
 *
 * This is the most common case — a client who was already in the system last month
 * and hasn't switched agents. Only a trail commission is calculated, applied to the
 * current month's full balance. No perimeter fee is added (that was already paid when
 * the client first joined).
 */
public class ExistingClientHandler extends AbstractCommissionHandler {

    /**
     * Processes the monthly trail commission for a returning client.
     *
     * @param session  the active Hibernate session for saving records
     * @param current  the balance record for this client this month
     * @param previous the balance record for this client last month (same agent)
     * @param month    the first day of the month being calculated
     * @param result   the running totals to update with this client's commission
     */
    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        // Track how much the balance changed this month (for display purposes)
        BigDecimal delta = current.getTotalBalance().subtract(previous.getTotalBalance());

        // Trail commission is always on the full current balance, not just the delta
        BigDecimal trailCommissionAmount = calcTrailCommission(current.getTotalBalance());

        persistTransaction(session, current, month, previous.getTotalBalance(), current.getTotalBalance(),
                delta, CommissionRates.TRAIL_COMMISSION_RATE, trailCommissionAmount,
                CommissionDirection.CREDIT, CommissionReason.TRAIL_COMMISSION);

        result.setTotalTrailCommission(result.getTotalTrailCommission().add(trailCommissionAmount));
        result.setExistingClients(result.getExistingClients() + 1);
        result.setTransactionCount(result.getTransactionCount() + 1);
    }
}
