package com.nexonsalary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_agent_history")
public class ClientAgentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private MemberAccount account;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "first_appearance_date", nullable = false)
    private LocalDate firstAppearanceDate;

    @Column(name = "leave_date")
    private LocalDate leaveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ClientStatus status;

    // Running total of scope commissions paid for this (account, agent) stint — used for clawback
    @Column(name = "total_scope_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPerimeterFeePaid = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ClientAgentHistory() {
    }

    public ClientAgentHistory(MemberAccount account, Agent agent, Member member,
                               LocalDate firstAppearanceDate, BigDecimal initialPerimeterFeePaid) {
        this.account = account;
        this.agent = agent;
        this.member = member;
        this.firstAppearanceDate = firstAppearanceDate;
        this.status = ClientStatus.ACTIVE;
        this.totalPerimeterFeePaid = initialPerimeterFeePaid;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public MemberAccount getAccount() { return account; }
    public void setAccount(MemberAccount account) { this.account = account; }

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getFirstAppearanceDate() { return firstAppearanceDate; }
    public void setFirstAppearanceDate(LocalDate firstAppearanceDate) { this.firstAppearanceDate = firstAppearanceDate; }

    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }

    public ClientStatus getStatus() { return status; }
    public void setStatus(ClientStatus status) { this.status = status; }

    public BigDecimal getTotalPerimeterFeePaid() { return totalPerimeterFeePaid; }
    public void setTotalPerimeterFeePaid(BigDecimal totalPerimeterFeePaid) { this.totalPerimeterFeePaid = totalPerimeterFeePaid; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
