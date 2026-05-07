package com.example.domain;

import java.util.UUID;

public class PostDepositCmd {
    private final String accountNumber;
    private final Money amount;

    public PostDepositCmd(String accountNumber, Money amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getAmount() {
        return amount;
    }
}
