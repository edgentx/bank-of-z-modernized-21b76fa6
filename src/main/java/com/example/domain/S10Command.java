package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record S10Command(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
}