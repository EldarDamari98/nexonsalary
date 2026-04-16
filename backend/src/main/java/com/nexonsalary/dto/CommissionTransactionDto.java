package com.nexonsalary.dto;

import java.math.BigDecimal;

public class CommissionTransactionDto {

    private Long id;
    private String memberName;
    private String nationalId;
    private String accountNumber;
    private String agentCode;
    private String agentName;
    private String balanceDate;
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private BigDecimal deltaBalance;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private String direction;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getBalanceDate() { return balanceDate; }
    public void setBalanceDate(String balanceDate) { this.balanceDate = balanceDate; }

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

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
