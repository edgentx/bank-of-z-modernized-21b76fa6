package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record InitiateTransferCmd(
    String transferId,
    String fromAccount,
    String toAccount,
    BigDecimal amount
) implements Command {}
