package com.nexonsalary.service;

import java.math.BigDecimal;

public final class CommissionRates {

    // 3,000 ILS per 1,000,000 ILS
    public static final BigDecimal SCOPE_RATE = new BigDecimal("0.003");

    // 250 ILS per 1,000,000 ILS
    public static final BigDecimal NIFRA_RATE = new BigDecimal("0.00025");

    public static final BigDecimal CLAWBACK_UNDER_12_MONTHS = new BigDecimal("0.50");
    public static final BigDecimal CLAWBACK_12_TO_24_MONTHS = new BigDecimal("0.25");

    private CommissionRates() {
    }
}
