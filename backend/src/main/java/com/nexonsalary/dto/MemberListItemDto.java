package com.nexonsalary.dto;

import java.math.BigDecimal;

public class MemberListItemDto {

    private Long memberId;
    private String memberName;
    private String nationalId;
    private long accountsCount;
    private BigDecimal latestBalance;
    private String agentName;
    private String lastBalanceDate;

    public MemberListItemDto() {
    }

    public MemberListItemDto(Long memberId,
                             String memberName,
                             String nationalId,
                             long accountsCount,
                             BigDecimal latestBalance,
                             String agentName,
                             String lastBalanceDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.nationalId = nationalId;
        this.accountsCount = accountsCount;
        this.latestBalance = latestBalance;
        this.agentName = agentName;
        this.lastBalanceDate = lastBalanceDate;
    }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public long getAccountsCount() { return accountsCount; }
    public void setAccountsCount(long accountsCount) { this.accountsCount = accountsCount; }

    public BigDecimal getLatestBalance() { return latestBalance; }
    public void setLatestBalance(BigDecimal latestBalance) { this.latestBalance = latestBalance; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getLastBalanceDate() { return lastBalanceDate; }
    public void setLastBalanceDate(String lastBalanceDate) { this.lastBalanceDate = lastBalanceDate; }
}