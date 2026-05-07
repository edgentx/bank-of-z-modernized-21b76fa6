package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Command for S-10: PostDepositCmd.
 * Credits funds to a specific account.
 */
public class PostDepositCommand {

    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;

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
}