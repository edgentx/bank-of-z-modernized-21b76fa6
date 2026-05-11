package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

/**
 * Command to update the status of an Account.
 */
public record UpdateAccountStatusCmd(
        String accountNumber,
        AccountStatus newStatus,
        BigDecimal currentBalance
) implements Command {}
