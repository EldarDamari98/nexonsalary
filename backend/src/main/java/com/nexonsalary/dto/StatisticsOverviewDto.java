package com.nexonsalary.dto;

import java.math.BigDecimal;

public class StatisticsOverviewDto {

    private BigDecimal totalAssets;
    private BigDecimal totalCommissionPaid;
    private long totalTransactions;
    private long activeAgents;

    public StatisticsOverviewDto() {
    }

    public StatisticsOverviewDto(BigDecimal totalAssets,
                                 BigDecimal totalCommissionPaid,
                                 long totalTransactions,
                                 long activeAgents) {
        this.totalAssets = totalAssets;
        this.totalCommissionPaid = totalCommissionPaid;
        this.totalTransactions = totalTransactions;
        this.activeAgents = activeAgents;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getTotalCommissionPaid() {
        return totalCommissionPaid;
    }

    public void setTotalCommissionPaid(BigDecimal totalCommissionPaid) {
        this.totalCommissionPaid = totalCommissionPaid;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getActiveAgents() {
        return activeAgents;
    }

    public void setActiveAgents(long activeAgents) {
        this.activeAgents = activeAgents;
    }
}