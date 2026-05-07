package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to initiate a transfer of funds between two accounts.
 */
public record InitiateTransferCmd(
    String transferId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    String currency,
    BigDecimal availableBalance // Used for validation within the aggregate context
) implements Command {}
