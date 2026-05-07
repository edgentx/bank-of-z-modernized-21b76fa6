package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class DepositPostedEvent {
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;
    private final String transactionId;

    public DepositPostedEvent(String transactionId, String accountNumber, BigDecimal amount, Currency currency) {
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositPostedEvent that = (DepositPostedEvent) o;
        return Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, amount, currency);
    }
}
