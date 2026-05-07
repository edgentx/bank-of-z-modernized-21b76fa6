package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, String currencyCode) {

    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currencyCode);
        // Standardize scale to 2 for financial math usually, but keeping it flexible here
        amount = amount.setScale(2, java.math.RoundingMode.HALF_EVEN);
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode);
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode);
    }

    public Money add(Money other) {
        if (!other.currencyCode.equals(this.currencyCode)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }
}
