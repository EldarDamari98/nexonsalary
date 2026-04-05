package com.nexonsalary.dto;

public class ImportSummaryDto {

    private int createdAgents;
    private int createdMembers;
    private int createdAccounts;
    private int createdBalances;
    private int updatedBalances;

    public ImportSummaryDto() {
    }

    public int getCreatedAgents() {
        return createdAgents;
    }

    public void setCreatedAgents(int createdAgents) {
        this.createdAgents = createdAgents;
    }

    public int getCreatedMembers() {
        return createdMembers;
    }

    public void setCreatedMembers(int createdMembers) {
        this.createdMembers = createdMembers;
    }

    public int getCreatedAccounts() {
        return createdAccounts;
    }

    public void setCreatedAccounts(int createdAccounts) {
        this.createdAccounts = createdAccounts;
    }

    public int getCreatedBalances() {
        return createdBalances;
    }

    public void setCreatedBalances(int createdBalances) {
        this.createdBalances = createdBalances;
    }

    public int getUpdatedBalances() {
        return updatedBalances;
    }

    public void setUpdatedBalances(int updatedBalances) {
        this.updatedBalances = updatedBalances;
    }
}