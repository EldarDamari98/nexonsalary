package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.*;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NewClientHandler extends AbstractCommissionHandler {

    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        BigDecimal perimeterFeeAmount = calcPerimeterFee(current.getTotalBalance());
        BigDecimal trailCommissionAmount = calcTrailCommission(current.getTotalBalance());

        ClientAgentHistory history = new ClientAgentHistory(
                current.getAccount(), current.getAgent(), current.getMember(), month, perimeterFeeAmount
        );
        session.persist(history);

        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                current.getTotalBalance(), CommissionRates.PERIMETER_FEE_RATE, perimeterFeeAmount,
                CommissionDirection.CREDIT, CommissionReason.PERIMETER_FEE_NEW);

        persistTransaction(session, current, month, BigDecimal.ZERO, current.getTotalBalance(),
                BigDecimal.ZERO, CommissionRates.TRAIL_COMMISSION_RATE, trailCommissionAmount,
                CommissionDirection.CREDIT, CommissionReason.TRAIL_COMMISSION);

        result.setNewClients(result.getNewClients() + 1);
        result.setTotalPerimeterFeeNew(result.getTotalPerimeterFeeNew().add(perimeterFeeAmount));
        result.setTotalTrailCommission(result.getTotalTrailCommission().add(trailCommissionAmount));
        result.setTransactionCount(result.getTransactionCount() + 2);
    }
}
