package com.example.domain;

import java.math.BigDecimal;

/**
 * Command for posting a deposit to a Transaction.
 */
public class PostDepositCmd {
    private String accountNumber;
    private BigDecimal amount;
    private String currency;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}