package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record PostWithdrawalCmd(UUID accountId, BigDecimal amount, Currency currency) {}
