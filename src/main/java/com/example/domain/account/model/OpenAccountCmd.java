package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.time.Instant;

public record OpenAccountCmd(
        String accountId,
        String accountNumber,
        AccountType accountType,
        BigDecimal openingBalance,
        Instant openedAt
) implements Command {}
