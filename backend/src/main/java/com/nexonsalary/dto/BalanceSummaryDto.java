package com.nexonsalary.dto;

import java.math.BigDecimal;

public class BalanceSummaryDto {

    private long totalRecords;
    private long uniqueMembers;
    private long uniqueAccounts;
    private BigDecimal totalAssets;

    public BalanceSummaryDto() {
    }

    public BalanceSummaryDto(long totalRecords,
                             long uniqueMembers,
                             long uniqueAccounts,
                             BigDecimal totalAssets) {
        this.totalRecords = totalRecords;
        this.uniqueMembers = uniqueMembers;
        this.uniqueAccounts = uniqueAccounts;
        this.totalAssets = totalAssets;
    }

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

    public long getUniqueMembers() { return uniqueMembers; }
    public void setUniqueMembers(long uniqueMembers) { this.uniqueMembers = uniqueMembers; }

    public long getUniqueAccounts() { return uniqueAccounts; }
    public void setUniqueAccounts(long uniqueAccounts) { this.uniqueAccounts = uniqueAccounts; }

    public BigDecimal getTotalAssets() { return totalAssets; }
    public void setTotalAssets(BigDecimal totalAssets) { this.totalAssets = totalAssets; }
}