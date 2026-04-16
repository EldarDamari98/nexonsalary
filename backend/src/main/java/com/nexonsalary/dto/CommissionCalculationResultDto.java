package com.nexonsalary.dto;

import java.math.BigDecimal;

public class CommissionCalculationResultDto {

    private int newClients;
    private int existingClients;
    private int leftClients;
    private int transferredClients;
    private BigDecimal totalScopeNew;
    private BigDecimal totalScopeDelta;
    private BigDecimal totalClawbacks;
    private BigDecimal totalNifra;
    private BigDecimal netCommission;
    private int transactionCount;
    private String message;

    public CommissionCalculationResultDto() {
        this.totalScopeNew = BigDecimal.ZERO;
        this.totalScopeDelta = BigDecimal.ZERO;
        this.totalClawbacks = BigDecimal.ZERO;
        this.totalNifra = BigDecimal.ZERO;
        this.netCommission = BigDecimal.ZERO;
    }

    public int getNewClients() { return newClients; }
    public void setNewClients(int newClients) { this.newClients = newClients; }

    public int getExistingClients() { return existingClients; }
    public void setExistingClients(int existingClients) { this.existingClients = existingClients; }

    public int getLeftClients() { return leftClients; }
    public void setLeftClients(int leftClients) { this.leftClients = leftClients; }

    public int getTransferredClients() { return transferredClients; }
    public void setTransferredClients(int transferredClients) { this.transferredClients = transferredClients; }

    public BigDecimal getTotalScopeNew() { return totalScopeNew; }
    public void setTotalScopeNew(BigDecimal totalScopeNew) { this.totalScopeNew = totalScopeNew; }

    public BigDecimal getTotalScopeDelta() { return totalScopeDelta; }
    public void setTotalScopeDelta(BigDecimal totalScopeDelta) { this.totalScopeDelta = totalScopeDelta; }

    public BigDecimal getTotalClawbacks() { return totalClawbacks; }
    public void setTotalClawbacks(BigDecimal totalClawbacks) { this.totalClawbacks = totalClawbacks; }

    public BigDecimal getTotalNifra() { return totalNifra; }
    public void setTotalNifra(BigDecimal totalNifra) { this.totalNifra = totalNifra; }

    public BigDecimal getNetCommission() { return netCommission; }
    public void setNetCommission(BigDecimal netCommission) { this.netCommission = netCommission; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
