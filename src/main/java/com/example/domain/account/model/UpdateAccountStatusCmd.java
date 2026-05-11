package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record UpdateAccountStatusCmd(
        String accountNumber,
        String newStatus,
        BigDecimal currentBalance,
        String accountType,
        boolean accountNumberImmutable
) implements Command {}
