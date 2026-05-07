package com.example.domain;

import java.math.BigDecimal;

public class PostDepositCmd {
    public String accountNumber;
    public BigDecimal amount;
    public String currency;

    // Default constructor for Cucumber/Gherkin mapping
    public PostDepositCmd() {}

    public PostDepositCmd(String accountNumber, BigDecimal amount, String currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }
}