package com.nexonsalary.dto;

public class ReasonBreakdownDto {

    private String reason;
    private long count;

    public ReasonBreakdownDto() {
    }

    public ReasonBreakdownDto(String reason, long count) {
        this.reason = reason;
        this.count = count;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}