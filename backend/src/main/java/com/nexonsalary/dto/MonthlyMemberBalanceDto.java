package com.nexonsalary.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyMemberBalanceDto {

    private LocalDate balanceDate;
    private String memberName;
    private String nationalId;
    private String agentCode;
    private String agentName;
    private BigDecimal totalBalance;

    public MonthlyMemberBalanceDto() {
    }

    public MonthlyMemberBalanceDto(LocalDate balanceDate, String memberName, String nationalId,
                                   String agentCode, String agentName, BigDecimal totalBalance) {
        this.balanceDate = balanceDate;
        this.memberName = memberName;
        this.nationalId = nationalId;
        this.agentCode = agentCode;
        this.agentName = agentName;
        this.totalBalance = totalBalance;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
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

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
