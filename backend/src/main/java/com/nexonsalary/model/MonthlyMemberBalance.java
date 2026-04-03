package com.nexonsalary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_member_balances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_member_agent_balance_date",
                        columnNames = {"member_id", "agent_id", "balance_date"}
                )
        }
)
public class MonthlyMemberBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(name = "total_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalBalance;

    @Column(name = "source_file_name", length = 255)
    private String sourceFileName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MonthlyMemberBalance() {
    }

    public MonthlyMemberBalance(Member member, Agent agent, LocalDate balanceDate,
                                BigDecimal totalBalance, String sourceFileName) {
        this.member = member;
        this.agent = agent;
        this.balanceDate = balanceDate;
        this.totalBalance = totalBalance;
        this.sourceFileName = sourceFileName;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}