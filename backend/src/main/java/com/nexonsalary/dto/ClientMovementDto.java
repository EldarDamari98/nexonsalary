package com.nexonsalary.dto;

public class ClientMovementDto {

    private String month;
    private long newClients;
    private long leftClients;
    private long transferredClients;

    public ClientMovementDto() {
    }

    public ClientMovementDto(String month,
                             long newClients,
                             long leftClients,
                             long transferredClients) {
        this.month = month;
        this.newClients = newClients;
        this.leftClients = leftClients;
        this.transferredClients = transferredClients;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getNewClients() {
        return newClients;
    }

    public void setNewClients(long newClients) {
        this.newClients = newClients;
    }

    public long getLeftClients() {
        return leftClients;
    }

    public void setLeftClients(long leftClients) {
        this.leftClients = leftClients;
    }

    public long getTransferredClients() {
        return transferredClients;
    }

    public void setTransferredClients(long transferredClients) {
        this.transferredClients = transferredClients;
    }
}