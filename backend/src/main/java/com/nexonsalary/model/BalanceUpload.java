package com.nexonsalary.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_uploads")
public class BalanceUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "imported_rows", nullable = false)
    private int importedRows;

    @Column(name = "created_agents", nullable = false)
    private int createdAgents;

    @Column(name = "created_members", nullable = false)
    private int createdMembers;

    @Column(name = "created_accounts", nullable = false)
    private int createdAccounts;

    @Column(name = "created_balances", nullable = false)
    private int createdBalances;

    @Column(name = "updated_balances", nullable = false)
    private int updatedBalances;

    public BalanceUpload() {
    }

    public BalanceUpload(String fileName,
                         int importedRows,
                         int createdAgents,
                         int createdMembers,
                         int createdAccounts,
                         int createdBalances,
                         int updatedBalances) {
        this.fileName = fileName;
        this.importedRows = importedRows;
        this.createdAgents = createdAgents;
        this.createdMembers = createdMembers;
        this.createdAccounts = createdAccounts;
        this.createdBalances = createdBalances;
        this.updatedBalances = updatedBalances;
    }

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
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