package com.nexonsalary.dto;

import java.math.BigDecimal;

public class AgentPerformanceDto {

    private Long agentId;
    private String agentCode;
    private String agentName;
    private BigDecimal netCommission;

    public AgentPerformanceDto() {
    }

    public AgentPerformanceDto(Long agentId,
                               String agentCode,
                               String agentName,
                               BigDecimal netCommission) {
        this.agentId = agentId;
        this.agentCode = agentCode;
        this.agentName = agentName;
        this.netCommission = netCommission;
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

    public BigDecimal getNetCommission() {
        return netCommission;
    }

    public void setNetCommission(BigDecimal netCommission) {
        this.netCommission = netCommission;
    }
}