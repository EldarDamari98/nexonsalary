package com.nexonsalary.model;

import jakarta.persistence.*;

/**
 * Represents a specific financial account belonging to a member and managed by an agent.
 *
 * This is the link between a person (Member) and an agent (Agent).
 * One member can have multiple accounts — for example:
 *   - Account "PEN-001" → pension fund, managed by Agent Moshe
 *   - Account "INS-002" → life insurance, managed by Agent Sarah
 *
 * Each account gets its own monthly balance record and its own commission calculations.
 * Think of MemberAccount as the contract between a client and an agent for a specific product.
 *
 * Extends BaseEntity to inherit the id and createdAt fields.
 */
@Entity
@Table(name = "member_accounts")
public class MemberAccount extends BaseEntity {

    /** The unique account number as it appears in the Excel files (e.g. "PEN-123456"). */
    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    /**
     * The person who owns this account.
     * Loaded lazily — Hibernate only fetches the Member from the DB when you call getMember().
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * The agent currently managing this account.
     * Loaded lazily — Hibernate only fetches the Agent from the DB when you call getAgent().
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    /** Required by Hibernate — do not use directly. */
    public MemberAccount() {
    }

    /**
     * Creates a new account linking a member to an agent.
     *
     * @param accountNumber the unique account identifier from the pension system
     * @param member        the person who owns this account
     * @param agent         the agent responsible for managing this account
     */
    public MemberAccount(String accountNumber, Member member, Agent agent) {
        this.accountNumber = accountNumber;
        this.member = member;
        this.agent = agent;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
