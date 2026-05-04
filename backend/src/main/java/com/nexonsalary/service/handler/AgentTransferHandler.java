package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.ClientAgentHistory;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

public class AgentTransferHandler extends AbstractCommissionHandler {

    private final CommissionHandler newClientHandler;

    public AgentTransferHandler(CommissionHandler newClientHandler) {
        this.newClientHandler = newClientHandler;
    }

    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        ClientAgentHistory oldHistory = findActiveHistory(
                session, previous.getAccount().getId(), previous.getAgent().getId()
        );
        if (oldHistory != null) {
            applyLeave(session, oldHistory, previous, month, result);
        }

        newClientHandler.handle(session, current, null, month, result);

        // Reclassify: this was a transfer, not a net-new client
        result.setNewClients(result.getNewClients() - 1);
        result.setTransferredClients(result.getTransferredClients() + 1);
    }
}
