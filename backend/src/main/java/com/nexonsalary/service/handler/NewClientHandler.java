package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Handles commission calculation for a brand new client — one who has never appeared before.
 *
 * When a client appears in the monthly Excel data for the first time (no previous balance record),
 * two things happen:
 *   1. A perimeter fee is charged on the full balance (agent earns for bringing in a new client)
 *   2. A trail commission is charged on the full balance (standard monthly fee)
 *
 * A ClientAgentHistory record is also created to start tracking how long this
 * client stays with the agent (needed for future clawback calculations).
 */
public class NewClientHandler extends AbstractCommissionHandler {

    /**
     * Processes commissions for a first-time client.
     *
     * @param session  the active Hibernate session for saving records
     * @param current  the balance record for this new client this month
     * @param previous always null for new clients — there is no previous month
     * @param month    the first day of the month being calculated
     * @param result   the running totals to update with this client's commissions
     */
    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        BigDecimal perimeterFeeAmount = calcPerimeterFee(current.getTotalBalance());
        BigDecimal trailCommissionAmount = calcTrailCommission(current.getTotalBalance());

        // Start tracking this client's tenure with the agent (needed for future clawback)
        ClientAgentHistory history = new ClientAgentHistory(
                current.getAccount(), current.getAgent(), current.getMember(), month, perimeterFeeAmount
        );
        session.persist(history);

        // Commission 1: Perimeter fee on the full balance for acquiring a new client
        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                current.getTotalBalance(), CommissionRates.PERIMETER_FEE_RATE, perimeterFeeAmount,
                CommissionDirection.CREDIT, CommissionReason.PERIMETER_FEE_NEW);

        // Commission 2: Monthly trail commission on the full balance
        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                BigDecimal.ZERO, CommissionRates.TRAIL_COMMISSION_RATE, trailCommissionAmount,
                CommissionDirection.CREDIT, CommissionReason.TRAIL_COMMISSION);

        // Update the summary totals
        result.setNewClients(result.getNewClients() + 1);
        result.setTotalPerimeterFeeNew(result.getTotalPerimeterFeeNew().add(perimeterFeeAmount));
        result.setTotalTrailCommission(result.getTotalTrailCommission().add(trailCommissionAmount));
        result.setTransactionCount(result.getTransactionCount() + 2);
    }
}
