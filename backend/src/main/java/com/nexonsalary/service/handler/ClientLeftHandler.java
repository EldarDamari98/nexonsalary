package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.ClientAgentHistory;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

/**
 * Handles accounts that were active last month but completely disappeared this month.
 *
 * If a client was ACTIVE but their account doesn't appear in the current month's
 * Excel file at all, we assume they left — either they closed the account or
 * transferred to a provider outside our system. The agent may owe a clawback
 * if the client didn't stay long enough.
 *
 * Note: unlike the other handlers, 'current' is always null here because the
 * client did not appear in this month's data. The 'previous' parameter carries
 * the client's last known balance.
 */
public class ClientLeftHandler extends AbstractCommissionHandler {

    /**
     * Marks the client as LEFT and applies a clawback penalty if applicable.
     *
     * @param session  the active Hibernate session for saving records
     * @param current  always null — the client did not appear in this month's data
     * @param previous the client's last known balance record (used for the clawback transaction)
     * @param month    the first day of the month being calculated
     * @param result   the running totals to update
     */
    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        // Look up the active history to find out how long the client was with this agent
        ClientAgentHistory history = findActiveHistory(
                session, previous.getAccount().getId(), previous.getAgent().getId()
        );

        if (history != null) {
            // Close the relationship and apply clawback if tenure < 24 months
            applyLeave(session, history, previous, month, result);
        }

        result.setLeftClients(result.getLeftClients() + 1);
    }
}
