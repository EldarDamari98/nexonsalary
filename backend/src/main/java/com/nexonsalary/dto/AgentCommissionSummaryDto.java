package com.nexonsalary.dto;

import java.math.BigDecimal;

public class AgentCommissionSummaryDto {

    private Long agentId;
    private String agentCode;
    private String agentName;
    private BigDecimal scopeNew;
    private BigDecimal scopeDelta;
    private BigDecimal clawbacks;
    private BigDecimal nifra;
    private BigDecimal netCommission;
    private int transactionCount;

    public AgentCommissionSummaryDto() {
        this.scopeNew = BigDecimal.ZERO;
        this.scopeDelta = BigDecimal.ZERO;
        this.clawbacks = BigDecimal.ZERO;
        this.nifra = BigDecimal.ZERO;
        this.netCommission = BigDecimal.ZERO;
    }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public BigDecimal getScopeNew() { return scopeNew; }
    public void setScopeNew(BigDecimal scopeNew) { this.scopeNew = scopeNew; }

    public BigDecimal getScopeDelta() { return scopeDelta; }
    public void setScopeDelta(BigDecimal scopeDelta) { this.scopeDelta = scopeDelta; }

    public BigDecimal getClawbacks() { return clawbacks; }
    public void setClawbacks(BigDecimal clawbacks) { this.clawbacks = clawbacks; }

    public BigDecimal getNifra() { return nifra; }
    public void setNifra(BigDecimal nifra) { this.nifra = nifra; }

    public BigDecimal getNetCommission() { return netCommission; }
    public void setNetCommission(BigDecimal netCommission) { this.netCommission = netCommission; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
