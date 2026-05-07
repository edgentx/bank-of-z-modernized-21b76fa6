package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Command to post a deposit to an account.
 * Fixed: Moved to its own file to satisfy compiler error.
 */
public class PostDepositCmd implements S10Command {

    private final UUID accountNumber;
    private final BigDecimal amount;
    private final Currency currency;

    public PostDepositCmd(UUID accountNumber, BigDecimal amount, Currency currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
