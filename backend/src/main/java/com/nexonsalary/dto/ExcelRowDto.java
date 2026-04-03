package com.nexonsalary.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExcelRowDto {

    private LocalDate balanceDate;
    private String memberName;
    private String nationalId;
    private String secondaryAgentCode;
    private String secondaryAgentName;
    private BigDecimal balance;

    public ExcelRowDto() {
    }

    public ExcelRowDto(LocalDate balanceDate, String memberName, String nationalId,
                       String secondaryAgentCode, String secondaryAgentName, BigDecimal balance) {
        this.balanceDate = balanceDate;
        this.memberName = memberName;
        this.nationalId = nationalId;
        this.secondaryAgentCode = secondaryAgentCode;
        this.secondaryAgentName = secondaryAgentName;
        this.balance = balance;
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

    public String getSecondaryAgentCode() {
        return secondaryAgentCode;
    }

    public void setSecondaryAgentCode(String secondaryAgentCode) {
        this.secondaryAgentCode = secondaryAgentCode;
    }

    public String getSecondaryAgentName() {
        return secondaryAgentName;
    }

    public void setSecondaryAgentName(String secondaryAgentName) {
        this.secondaryAgentName = secondaryAgentName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}