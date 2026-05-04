package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

public interface CommissionHandler {

    void handle(Session session,
                MonthlyMemberBalance current,
                MonthlyMemberBalance previous,
                LocalDate month,
                CommissionCalculationResultDto result);
}
