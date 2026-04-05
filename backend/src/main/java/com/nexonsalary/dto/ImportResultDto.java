package com.nexonsalary.dto;

public class ImportResultDto {

    private boolean success;
    private int importedRows;
    private int createdAgents;
    private int createdMembers;
    private int createdAccounts;
    private int createdBalances;
    private int updatedBalances;
    private String message;

    public ImportResultDto() {
    }

    public ImportResultDto(boolean success,
                           int importedRows,
                           int createdAgents,
                           int createdMembers,
                           int createdAccounts,
                           int createdBalances,
                           int updatedBalances,
                           String message) {
        this.success = success;
        this.importedRows = importedRows;
        this.createdAgents = createdAgents;
        this.createdMembers = createdMembers;
        this.createdAccounts = createdAccounts;
        this.createdBalances = createdBalances;
        this.updatedBalances = updatedBalances;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getImportedRows() {
        return importedRows;
    }

    public void setImportedRows(int importedRows) {
        this.importedRows = importedRows;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}