package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.ClientAgentHistory;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

public class ClientLeftHandler extends AbstractCommissionHandler {

    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        // 'previous' is the last known balance; 'current' is null (client did not appear this month)
        ClientAgentHistory history = findActiveHistory(
                session, previous.getAccount().getId(), previous.getAgent().getId()
        );
        if (history != null) {
            applyLeave(session, history, previous, month, result);
        }
        result.setLeftClients(result.getLeftClients() + 1);
    }
}
