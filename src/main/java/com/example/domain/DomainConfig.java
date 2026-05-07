package com.example.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Configuration class for Domain constraints.
 * Can be injected as a Spring Bean.
 */
public class DomainConfig {

    private final BigDecimal maxTransactionAmount;

    public DomainConfig(BigDecimal maxTransactionAmount) {
        this.maxTransactionAmount = maxTransactionAmount;
    }

    public BigDecimal getMaxTransactionAmount() {
        return maxTransactionAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainConfig that = (DomainConfig) o;
        return Objects.equals(maxTransactionAmount, that.maxTransactionAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maxTransactionAmount);
    }
}
