package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record S10Event(UUID eventId, UUID transactionId, String accountNumber, BigDecimal amount, String currency, Instant timestamp) {
    public S10Event {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}