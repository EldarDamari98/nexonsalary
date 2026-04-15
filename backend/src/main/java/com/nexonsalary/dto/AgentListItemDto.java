package com.nexonsalary.dto;

import java.math.BigDecimal;

public class AgentListItemDto {

    private Long agentId;
    private String agentCode;
    private String agentName;
    private long membersCount;
    private long accountsCount;
    private BigDecimal totalAssets;
    private String latestBalanceDate;

    public AgentListItemDto() {
    }

    public AgentListItemDto(Long agentId,
                            String agentCode,
                            String agentName,
                            long membersCount,
                            long accountsCount,
                            BigDecimal totalAssets,
                            String latestBalanceDate) {
        this.agentId = agentId;
        this.agentCode = agentCode;
        this.agentName = agentName;
        this.membersCount = membersCount;
        this.accountsCount = accountsCount;
        this.totalAssets = totalAssets;
        this.latestBalanceDate = latestBalanceDate;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
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

    public long getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(long membersCount) {
        this.membersCount = membersCount;
    }

    public long getAccountsCount() {
        return accountsCount;
    }

    public void setAccountsCount(long accountsCount) {
        this.accountsCount = accountsCount;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public String getLatestBalanceDate() {
        return latestBalanceDate;
    }

    public void setLatestBalanceDate(String latestBalanceDate) {
        this.latestBalanceDate = latestBalanceDate;
    }
}