package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class DepositPostedEvent {
    public final UUID transactionId;
    public final String accountNumber;
    public final BigDecimal amount;
    public final String currency;

    public DepositPostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }
}