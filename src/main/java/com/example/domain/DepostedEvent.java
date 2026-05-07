package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record DepostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
}