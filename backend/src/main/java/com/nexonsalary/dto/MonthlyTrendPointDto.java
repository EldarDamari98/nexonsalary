package com.nexonsalary.dto;

import java.math.BigDecimal;

public class MonthlyTrendPointDto {

    private String month;
    private BigDecimal value;

    public MonthlyTrendPointDto() {
    }

    public MonthlyTrendPointDto(String month, BigDecimal value) {
        this.month = month;
        this.value = value;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}