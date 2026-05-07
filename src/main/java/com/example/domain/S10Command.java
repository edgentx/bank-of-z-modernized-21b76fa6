package com.example.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class S10Command {
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public S10Command(String accountNumber, BigDecimal amount, String currency) {
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
        S10Command s10Command = (S10Command) o;
        return Objects.equals(accountNumber, s10Command.accountNumber) &&
                Objects.equals(amount, s10Command.amount) &&
                Objects.equals(currency, s10Command.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, amount, currency);
    }
}
