package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.CommissionDirection;
import com.nexonsalary.model.CommissionReason;
import com.nexonsalary.model.MonthlyMemberBalance;
import com.nexonsalary.service.CommissionRates;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExistingClientHandler extends AbstractCommissionHandler {

    @Override
    public void handle(Session session, MonthlyMemberBalance current,
                       MonthlyMemberBalance previous, LocalDate month,
                       CommissionCalculationResultDto result) {
        BigDecimal delta = current.getTotalBalance().subtract(previous.getTotalBalance());
        BigDecimal trailCommissionAmount = calcTrailCommission(current.getTotalBalance());

        persistTransaction(session, current, month, previous.getTotalBalance(), current.getTotalBalance(),
                delta, CommissionRates.TRAIL_COMMISSION_RATE, trailCommissionAmount,
                CommissionDirection.CREDIT, CommissionReason.TRAIL_COMMISSION);

        result.setTotalTrailCommission(result.getTotalTrailCommission().add(trailCommissionAmount));
        result.setExistingClients(result.getExistingClients() + 1);
        result.setTransactionCount(result.getTransactionCount() + 1);
    }
}
