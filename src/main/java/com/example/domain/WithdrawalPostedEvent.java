package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class WithdrawalPostedEvent {
    private final UUID eventId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public WithdrawalPostedEvent(UUID eventId, String accountNumber, BigDecimal amount, String currency) {
        this.eventId = eventId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
