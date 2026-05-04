package com.nexonsalary.model;

import jakarta.persistence.*;

/**
 * Represents a pension/insurance agent in the system.
 *
 * An agent manages one or more client accounts and earns commissions
 * based on the balances of those accounts. Each agent has a unique code
 * (assigned by the pension provider) and a display name.
 *
 * Extends BaseEntity to inherit the id and createdAt fields.
 */
@Entity
@Table(name = "agents")
public class Agent extends BaseEntity {

    /** The unique code that identifies this agent in the pension system (e.g. "2-9055"). */
    @Column(name = "agent_code", nullable = false, unique = true, length = 50)
    private String agentCode;

    /** The full display name of the agent (e.g. "אופק שירותים פנסיוניים"). */
    @Column(name = "agent_name", nullable = false, length = 255)
    private String agentName;

    /** Whether this agent is currently active. Inactive agents are hidden from most views. */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /** Required by Hibernate — do not use directly. */
    public Agent() {
    }

    /**
     * Creates a new active agent with the given code and name.
     *
     * @param agentCode the unique code assigned to this agent by the pension provider
     * @param agentName the full display name of the agent
     */
    public Agent(String agentCode, String agentName) {
        this.agentCode = agentCode;
        this.agentName = agentName;
        this.active = true;
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
