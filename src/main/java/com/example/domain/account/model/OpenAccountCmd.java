package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to open a new bank account.
 * Story: S-5 Implement OpenAccountCmd on Account.
 */
public record OpenAccountCmd(
        String accountId,
        String customerId,
        String accountType,
        BigDecimal initialDeposit,
        String sortCode
) implements Command {}
