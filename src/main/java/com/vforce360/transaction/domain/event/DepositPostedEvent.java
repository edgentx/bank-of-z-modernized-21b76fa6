package com.vforce360.transaction.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DepositPostedEvent {
    private final UUID transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDateTime postedAt;

    public DepositPostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currency, LocalDateTime postedAt) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.postedAt = postedAt;
    }

    public UUID getTransactionId() {
        return transactionId;
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

    public LocalDateTime getPostedAt() {
        return postedAt;
    }
}
