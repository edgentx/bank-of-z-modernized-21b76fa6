package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record InitiateTransferCmd(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency
) implements Command {}