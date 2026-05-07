package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record CompleteTransferCmd(
        String transferId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        BigDecimal sourceBalance
) implements Command {}
