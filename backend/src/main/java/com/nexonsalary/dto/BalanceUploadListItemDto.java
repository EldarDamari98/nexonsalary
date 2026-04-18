package com.nexonsalary.dto;

public class BalanceUploadListItemDto {

    private Long uploadId;
    private String fileName;
    private String uploadedAt;
    private int importedRows;
    private int createdAgents;
    private int createdMembers;
    private int createdAccounts;
    private int createdBalances;
    private int updatedBalances;

    public BalanceUploadListItemDto() {
    }

    public BalanceUploadListItemDto(Long uploadId,
                                    String fileName,
                                    String uploadedAt,
                                    int importedRows,
                                    int createdAgents,
                                    int createdMembers,
                                    int createdAccounts,
                                    int createdBalances,
                                    int updatedBalances) {
        this.uploadId = uploadId;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.importedRows = importedRows;
        this.createdAgents = createdAgents;
        this.createdMembers = createdMembers;
        this.createdAccounts = createdAccounts;
        this.createdBalances = createdBalances;
        this.updatedBalances = updatedBalances;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
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
}