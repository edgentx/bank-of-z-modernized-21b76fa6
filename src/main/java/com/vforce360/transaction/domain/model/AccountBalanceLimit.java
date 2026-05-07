package com.vforce360.transaction.domain.model;

import java.math.BigDecimal;

/**
 * Value object representing constraints on an account balance.
 * Currently used for type safety in the domain, though logic resides in the Aggregate/Ports.
 */
public class AccountBalanceLimit {
    private final BigDecimal min;
    private final BigDecimal max;

    public AccountBalanceLimit(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public BigDecimal getMin() { return min; }
    public BigDecimal getMax() { return max; }
}
