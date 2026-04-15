package com.nexonsalary.dto;

import java.math.BigDecimal;

public class DashboardSummaryDto {

    private long totalAgents;
    private long totalMembers;
    private long totalAccounts;
    private BigDecimal totalAssets;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(long totalAgents,
                               long totalMembers,
                               long totalAccounts,
                               BigDecimal totalAssets) {
        this.totalAgents = totalAgents;
        this.totalMembers = totalMembers;
        this.totalAccounts = totalAccounts;
        this.totalAssets = totalAssets;
    }

    public long getTotalAgents() {
        return totalAgents;
    }

    public void setTotalAgents(long totalAgents) {
        this.totalAgents = totalAgents;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }
}