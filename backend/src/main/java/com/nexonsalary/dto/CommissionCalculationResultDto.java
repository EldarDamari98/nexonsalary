package com.nexonsalary.dto;

import java.math.BigDecimal;

public class CommissionCalculationResultDto {

    private int newClients;
    private int existingClients;
    private int leftClients;
    private int transferredClients;
    private BigDecimal totalPerimeterFeeNew;
    private BigDecimal totalPerimeterFeeDelta;
    private BigDecimal totalClawbacks;
    private BigDecimal totalTrailCommission;
    private BigDecimal netCommission;
    private int transactionCount;
    private String message;

    public CommissionCalculationResultDto() {
        this.totalPerimeterFeeNew = BigDecimal.ZERO;
        this.totalPerimeterFeeDelta = BigDecimal.ZERO;
        this.totalClawbacks = BigDecimal.ZERO;
        this.totalTrailCommission = BigDecimal.ZERO;
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

    public BigDecimal getTotalPerimeterFeeNew() { return totalPerimeterFeeNew; }
    public void setTotalPerimeterFeeNew(BigDecimal totalPerimeterFeeNew) { this.totalPerimeterFeeNew = totalPerimeterFeeNew; }

    public BigDecimal getTotalPerimeterFeeDelta() { return totalPerimeterFeeDelta; }
    public void setTotalPerimeterFeeDelta(BigDecimal totalPerimeterFeeDelta) { this.totalPerimeterFeeDelta = totalPerimeterFeeDelta; }

    public BigDecimal getTotalClawbacks() { return totalClawbacks; }
    public void setTotalClawbacks(BigDecimal totalClawbacks) { this.totalClawbacks = totalClawbacks; }

    public BigDecimal getTotalTrailCommission() { return totalTrailCommission; }
    public void setTotalTrailCommission(BigDecimal totalTrailCommission) { this.totalTrailCommission = totalTrailCommission; }

    public BigDecimal getNetCommission() { return netCommission; }
    public void setNetCommission(BigDecimal netCommission) { this.netCommission = netCommission; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
