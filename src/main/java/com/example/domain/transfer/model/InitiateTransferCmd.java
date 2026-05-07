package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to initiate a transfer between two accounts.
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount
) implements Command {}
