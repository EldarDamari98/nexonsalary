package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.ClientAgentHistory;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

/**
 * Handles commission calculation when a client switches from one agent to another.
 *
 * An agent transfer is detected when the same account appears this month with a
 * different agent than last month. Two things need to happen:
 *   1. The old agent's relationship is closed — apply clawback if tenure was short
 *   2. The new agent is treated exactly like a new client — perimeter fee + trail commission
 *
 * This handler reuses NewClientHandler for step 2 (via constructor injection),
 * which is an example of composition — using an existing object instead of copying its code.
 */
public class AgentTransferHandler extends AbstractCommissionHandler {

    /**
     * The handler responsible for setting up the new agent's commissions.
     * Injected at construction so we don't duplicate the new-client logic here.
     */
    private final CommissionHandler newClientHandler;

    /**
     * Creates a transfer handler that delegates new-client setup to the given handler.
     *
     * @param newClientHandler the handler to use when registering the new agent's commission
     */
    public AgentTransferHandler(CommissionHandler newClientHandler) {
        this.newClientHandler = newClientHandler;
    }

    /**
     * Closes the old agent's relationship and opens a new one for the new agent.
     *
     * @param session  the active Hibernate session for saving records
     * @param current  the balance record showing the account under the NEW agent this month
     * @param previous the balance record showing the account under the OLD agent last month
     * @param month    the first day of the month being calculated
     * @param result   the running totals to update
     */
    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        // Step 1: Close out the old agent — apply clawback if the client didn't stay long enough
        ClientAgentHistory oldHistory = findActiveHistory(
                session, previous.getAccount().getId(), previous.getAgent().getId()
        );
        if (oldHistory != null) {
            applyLeave(session, oldHistory, previous, month, result);
        }

        // Step 2: Treat the new agent exactly like a brand new client relationship
        newClientHandler.handle(session, current, null, month, result);

        // Reclassify the counter: this was a transfer, not a genuinely new client
        result.setNewClients(result.getNewClients() - 1);
        result.setTransferredClients(result.getTransferredClients() + 1);
    }
}
