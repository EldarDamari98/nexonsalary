package com.nexonsalary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tracks the relationship between a client account and an agent over time.
 *
 * This is the audit trail that makes clawback calculations possible.
 * Every time a client is assigned to an agent for the first time, a new history
 * record is created with status ACTIVE. When the client leaves or switches agents,
 * the record is updated to status LEFT with a leave date.
 *
 * The key field for clawback is totalPerimeterFeePaid — it accumulates all the
 * perimeter fees the agent earned from this client. If the client leaves within
 * 12-24 months, a portion of that amount is taken back from the agent.
 *
 * Extends BaseEntity to inherit the id and createdAt fields.
 * Overrides prePersist() to also set the updatedAt timestamp on creation.
 */
@Entity
@Table(name = "client_agent_history")
public class ClientAgentHistory extends BaseEntity {

    /** The account this history record is tracking. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private MemberAccount account;

    /** The agent who managed this account during this period. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    /** The client who owns this account. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** The first month this client appeared under this agent. Used to calculate tenure for clawback. */
    @Column(name = "first_appearance_date", nullable = false)
    private LocalDate firstAppearanceDate;

    /** The month the client left this agent. Null if the client is still active. */
    @Column(name = "leave_date")
    private LocalDate leaveDate;

    /**
     * Whether the client is currently with this agent.
     * ACTIVE = still here, LEFT = moved away or account closed.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ClientStatus status;

    /**
     * The running total of all perimeter fees this agent has earned from this client.
     * This is the pool that clawback penalties are calculated against.
     * For example, if the agent earned 1,000 ILS in perimeter fees and the client
     * leaves within 12 months, the agent owes back 500 ILS (50% clawback).
     */
    @Column(name = "total_scope_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPerimeterFeePaid = BigDecimal.ZERO;

    /** The last time this record was updated (e.g. when the client left). */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Required by Hibernate — do not use directly. */
    public ClientAgentHistory() {
    }

    /**
     * Creates a new ACTIVE history record when a client first appears under an agent.
     *
     * @param account                 the account being tracked
     * @param agent                   the agent now managing this account
     * @param member                  the client who owns the account
     * @param firstAppearanceDate     the month this relationship started
     * @param initialPerimeterFeePaid the first perimeter fee earned (from the new client commission)
     */
    public ClientAgentHistory(MemberAccount account, Agent agent, Member member,
                               LocalDate firstAppearanceDate, BigDecimal initialPerimeterFeePaid) {
        this.account = account;
        this.agent = agent;
        this.member = member;
        this.firstAppearanceDate = firstAppearanceDate;
        this.status = ClientStatus.ACTIVE;
        this.totalPerimeterFeePaid = initialPerimeterFeePaid;
    }

    /**
     * Called automatically before inserting a new record.
     * Calls the parent to set createdAt, then also sets updatedAt to the same time.
     */
    @Override
    @PrePersist
    public void prePersist() {
        super.prePersist();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Called automatically before updating an existing record.
     * Refreshes updatedAt so we always know when the record was last changed.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public MemberAccount getAccount() { return account; }
    public void setAccount(MemberAccount account) { this.account = account; }

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getFirstAppearanceDate() { return firstAppearanceDate; }
    public void setFirstAppearanceDate(LocalDate d) { this.firstAppearanceDate = d; }

    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }

    public ClientStatus getStatus() { return status; }
    public void setStatus(ClientStatus status) { this.status = status; }

    public BigDecimal getTotalPerimeterFeePaid() { return totalPerimeterFeePaid; }
    public void setTotalPerimeterFeePaid(BigDecimal v) { this.totalPerimeterFeePaid = v; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
