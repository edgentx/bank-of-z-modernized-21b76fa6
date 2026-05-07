package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class PostDepositCmd {
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;

    public PostDepositCmd(String accountNumber, BigDecimal amount, Currency currency) {
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

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostDepositCmd that = (PostDepositCmd) o;
        return Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, amount, currency);
    }
}
