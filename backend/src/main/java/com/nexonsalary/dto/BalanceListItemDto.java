package com.nexonsalary.dto;

import java.math.BigDecimal;

public class BalanceListItemDto {

    private Long balanceId;
    private String memberName;
    private String nationalId;
    private String accountNumber;
    private String agentName;
    private String balanceDate;
    private BigDecimal totalBalance;

    public BalanceListItemDto() {
    }

    public BalanceListItemDto(Long balanceId,
                              String memberName,
                              String nationalId,
                              String accountNumber,
                              String agentName,
                              String balanceDate,
                              BigDecimal totalBalance) {
        this.balanceId = balanceId;
        this.memberName = memberName;
        this.nationalId = nationalId;
        this.accountNumber = accountNumber;
        this.agentName = agentName;
        this.balanceDate = balanceDate;
        this.totalBalance = totalBalance;
    }

    public Long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(String balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}