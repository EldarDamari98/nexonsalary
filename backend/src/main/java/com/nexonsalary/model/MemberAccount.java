package com.nexonsalary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "member_accounts")
public class MemberAccount extends BaseEntity {

    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public MemberAccount() {
    }

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