package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record WithdrawalPostedEvent(UUID transactionId, UUID accountId, BigDecimal amount, Currency currency) {}
