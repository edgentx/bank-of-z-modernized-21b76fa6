package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Command for Posting a Withdrawal.
 * Part of Story S-11.
 */
public class S11Command {

    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;

    public S11Command() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S11Command that = (S11Command) o;
        return Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, amount, currency);
    }
}
