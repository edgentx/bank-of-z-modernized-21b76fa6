package com.example.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple In-Memory Aggregate for Testing Account Constraints.
 */
public class Account {
    
    private final String accountNumber;
    private BigDecimal balance;
    private final Map<String, Object> metadata = new HashMap<>();

    public Account(String accountNumber, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}