package com.vforce360.transaction.domain.event;

import java.math.BigDecimal;
import java.util.Currency;

public class WithdrawalPostedEvent {
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;

    public WithdrawalPostedEvent(String accountNumber, BigDecimal amount, Currency currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
}