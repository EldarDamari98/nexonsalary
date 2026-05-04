package com.nexonsalary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "commission_transactions")
public class CommissionTransaction extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private MemberAccount account;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(name = "previous_balance", precision = 18, scale = 2)
    private BigDecimal previousBalance;

    @Column(name = "current_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "delta_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal deltaBalance;

    @Column(name = "commission_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private CommissionDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    private CommissionReason reason;

    public CommissionTransaction() {
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public MemberAccount getAccount() {
        return account;
    }

    public void setAccount(MemberAccount account) {
        this.account = account;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getDeltaBalance() {
        return deltaBalance;
    }

    public void setDeltaBalance(BigDecimal deltaBalance) {
        this.deltaBalance = deltaBalance;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public CommissionDirection getDirection() {
        return direction;
    }

    public void setDirection(CommissionDirection direction) {
        this.direction = direction;
    }

    public CommissionReason getReason() {
        return reason;
    }

    public void setReason(CommissionReason reason) {
        this.reason = reason;
    }

}