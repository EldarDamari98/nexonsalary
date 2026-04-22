package com.nexonsalary.dto;

import java.math.BigDecimal;

public class AgentCommissionSummaryDto {

    private Long agentId;
    private String agentCode;
    private String agentName;
    private BigDecimal perimeterFeeNew;
    private BigDecimal perimeterFeeDelta;
    private BigDecimal clawbacks;
    private BigDecimal trailCommission;
    private BigDecimal netCommission;
    private int transactionCount;

    public AgentCommissionSummaryDto() {
        this.perimeterFeeNew = BigDecimal.ZERO;
        this.perimeterFeeDelta = BigDecimal.ZERO;
        this.clawbacks = BigDecimal.ZERO;
        this.trailCommission = BigDecimal.ZERO;
        this.netCommission = BigDecimal.ZERO;
    }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public BigDecimal getPerimeterFeeNew() { return perimeterFeeNew; }
    public void setPerimeterFeeNew(BigDecimal perimeterFeeNew) { this.perimeterFeeNew = perimeterFeeNew; }

    public BigDecimal getPerimeterFeeDelta() { return perimeterFeeDelta; }
    public void setPerimeterFeeDelta(BigDecimal perimeterFeeDelta) { this.perimeterFeeDelta = perimeterFeeDelta; }

    public BigDecimal getClawbacks() { return clawbacks; }
    public void setClawbacks(BigDecimal clawbacks) { this.clawbacks = clawbacks; }

    public BigDecimal getTrailCommission() { return trailCommission; }
    public void setTrailCommission(BigDecimal trailCommission) { this.trailCommission = trailCommission; }

    public BigDecimal getNetCommission() { return netCommission; }
    public void setNetCommission(BigDecimal netCommission) { this.netCommission = netCommission; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
