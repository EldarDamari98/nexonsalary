package com.nexonsalary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Records a single commission payment or deduction for a specific account in a given month.
 *
 * Every time the commission calculation runs for a month, it generates one or more
 * CommissionTransaction records per account. Each transaction captures:
 *   - Which account earned or was charged a commission
 *   - How much the balance was and how it changed
 *   - The commission amount and rate applied
 *   - Whether this is money going TO the agent (CREDIT) or taken FROM the agent (DEBIT/clawback)
 *   - The business reason for the commission (new client, trail, clawback, etc.)
 *
 * These records are the final financial output of the system — the "salary" for each agent.
 *
 * Extends BaseEntity to inherit the id and createdAt fields.
 */
@Entity
@Table(name = "commission_transactions")
public class CommissionTransaction extends BaseEntity {

    /** The client this commission relates to. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** The agent receiving or being charged this commission. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    /** The specific account this commission was calculated on. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private MemberAccount account;

    /** The first day of the month this commission belongs to (e.g. 2025-01-01). */
    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    /** The account balance in the previous month. Null for brand new clients. */
    @Column(name = "previous_balance", precision = 18, scale = 2)
    private BigDecimal previousBalance;

    /** The account balance in the current month (used as the base for commission calculation). */
    @Column(name = "current_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentBalance;

    /** The difference between current and previous balance (positive = growth, negative = shrinkage). */
    @Column(name = "delta_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal deltaBalance;

    /** The percentage rate applied to calculate this commission (e.g. 0.003 for perimeter fee). */
    @Column(name = "commission_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal commissionRate;

    /** The final commission amount in ILS (Israeli Shekels). */
    @Column(name = "commission_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal commissionAmount;

    /**
     * Whether this commission adds money to the agent (CREDIT) or takes money away (DEBIT).
     * Clawback penalties are always DEBIT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private CommissionDirection direction;

    /**
     * The business reason that caused this commission to be generated.
     * Possible values: PERIMETER_FEE_NEW, PERIMETER_FEE_DELTA, TRAIL_COMMISSION, PERIMETER_FEE_CLAWBACK.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    private CommissionReason reason;

    /** Required by Hibernate — do not use directly. */
    public CommissionTransaction() {
    }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }

    public MemberAccount getAccount() { return account; }
    public void setAccount(MemberAccount account) { this.account = account; }

    public LocalDate getBalanceDate() { return balanceDate; }
    public void setBalanceDate(LocalDate balanceDate) { this.balanceDate = balanceDate; }

    public BigDecimal getPreviousBalance() { return previousBalance; }
    public void setPreviousBalance(BigDecimal previousBalance) { this.previousBalance = previousBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public BigDecimal getDeltaBalance() { return deltaBalance; }
    public void setDeltaBalance(BigDecimal deltaBalance) { this.deltaBalance = deltaBalance; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public CommissionDirection getDirection() { return direction; }
    public void setDirection(CommissionDirection direction) { this.direction = direction; }

    public CommissionReason getReason() { return reason; }
    public void setReason(CommissionReason reason) { this.reason = reason; }
}
