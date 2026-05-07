package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to initiate a transfer of funds between two accounts.
 * Story: S-13
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency
) implements Command {}
