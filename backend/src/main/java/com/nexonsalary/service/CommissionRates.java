package com.nexonsalary.service;

import java.math.BigDecimal;

/**
 * Central place that stores all commission rates used in the system.
 *
 * By keeping all rates here, we avoid "magic numbers" scattered across the code.
 * If rates ever change, this is the only file that needs to be updated.
 *
 * The class is final and has a private constructor to prevent instantiation —
 * these are constants, not something you create objects from.
 */
public final class CommissionRates {

    /**
     * Perimeter fee rate: 0.3% of the account balance.
     * Applied when a new client joins or when their balance increases.
     * In practice: 3,000 ILS per 1,000,000 ILS managed.
     */
    public static final BigDecimal PERIMETER_FEE_RATE = new BigDecimal("0.003");

    /**
     * Trail commission rate: 0.025% of the account balance.
     * Applied every month on every active account, regardless of balance changes.
     * In practice: 250 ILS per 1,000,000 ILS managed.
     */
    public static final BigDecimal TRAIL_COMMISSION_RATE = new BigDecimal("0.00025");

    /**
     * Clawback penalty for clients who leave within the first 12 months.
     * The agent must return 50% of all perimeter fees they earned from this client.
     */
    public static final BigDecimal CLAWBACK_UNDER_12_MONTHS = new BigDecimal("0.50");

    /**
     * Clawback penalty for clients who leave between 12 and 24 months.
     * The agent must return 25% of all perimeter fees they earned from this client.
     */
    public static final BigDecimal CLAWBACK_12_TO_24_MONTHS = new BigDecimal("0.25");

    /** Prevent instantiation — this class is only for constants. */
    private CommissionRates() {
    }
}
