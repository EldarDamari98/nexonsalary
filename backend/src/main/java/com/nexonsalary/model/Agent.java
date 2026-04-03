package com.nexonsalary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_code", nullable = false, unique = true, length = 50)
    private String agentCode;

    @Column(name = "agent_name", nullable = false, length = 255)
    private String agentName;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Agent() {
    }

    public Agent(String agentCode, String agentName) {
        this.agentCode = agentCode;
        this.agentName = agentName;
        this.active = true;
    }

    public Long getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}