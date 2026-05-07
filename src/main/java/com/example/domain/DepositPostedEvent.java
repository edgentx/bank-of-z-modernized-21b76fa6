package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositPostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currency, LocalDateTime timestamp) {
    public DepositPostedEvent {
        // Default timestamp to now if not provided, though usually handled by aggregate
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}