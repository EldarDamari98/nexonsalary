package com.nexonsalary.service.handler;

import com.nexonsalary.dto.CommissionCalculationResultDto;
import com.nexonsalary.model.MonthlyMemberBalance;
import org.hibernate.Session;

import java.time.LocalDate;

/**
 * The contract that every commission handler must follow.
 *
 * This interface is the foundation of the Strategy Pattern used in commission calculation.
 * Instead of one giant if/else block, each scenario (new client, existing client,
 * agent transfer, client left) has its own class that implements this interface.
 *
 * The main calculation service just calls handler.handle() without knowing
 * which concrete handler it's talking to — that's polymorphism in action.
 */
public interface CommissionHandler {

    /**
     * Processes the commission logic for a single account in a given month.
     *
     * @param session  the active Hibernate database session used to save transactions
     * @param current  the balance record for this account in the current month
     *                 (null for ClientLeftHandler — the client didn't appear this month)
     * @param previous the balance record for this account in the previous month
     *                 (null for NewClientHandler — the client didn't exist before)
     * @param month    the first day of the month being calculated (e.g. 2025-01-01)
     * @param result   the running totals object that accumulates commission amounts and counts
     */
    void handle(Session session,
                MonthlyMemberBalance current,
                MonthlyMemberBalance previous,
                LocalDate month,
                CommissionCalculationResultDto result);
}
