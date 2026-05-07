package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositPostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
}