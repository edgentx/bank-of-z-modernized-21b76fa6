package com.example.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command for posting a withdrawal (S-11).
 * Immutable command object.
 */
public class S11Command {
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public S11Command(String accountNumber, BigDecimal amount, String currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S11Command s11Command = (S11Command) o;
        return Objects.equals(accountNumber, s11Command.accountNumber) && Objects.equals(amount, s11Command.amount) && Objects.equals(currency, s11Command.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, amount, currency);
    }
}
