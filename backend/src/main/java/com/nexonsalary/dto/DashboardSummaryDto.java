package com.nexonsalary.dto;

import java.math.BigDecimal;

public class DashboardSummaryDto {

    private long totalAgents;
    private long totalMembers;
    private long totalAccounts;
    private BigDecimal totalAssets;
    private BigDecimal currentMonthlySalary;
    private String currentSalaryMonth;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(long totalAgents,
                               long totalMembers,
                               long totalAccounts,
                               BigDecimal totalAssets,
                               BigDecimal currentMonthlySalary,
                               String currentSalaryMonth) {
        this.totalAgents = totalAgents;
        this.totalMembers = totalMembers;
        this.totalAccounts = totalAccounts;
        this.totalAssets = totalAssets;
        this.currentMonthlySalary = currentMonthlySalary;
        this.currentSalaryMonth = currentSalaryMonth;
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

    public BigDecimal getCurrentMonthlySalary() {
        return currentMonthlySalary;
    }

    public void setCurrentMonthlySalary(BigDecimal currentMonthlySalary) {
        this.currentMonthlySalary = currentMonthlySalary;
    }

    public String getCurrentSalaryMonth() {
        return currentSalaryMonth;
    }

    public void setCurrentSalaryMonth(String currentSalaryMonth) {
        this.currentSalaryMonth = currentSalaryMonth;
    }
}