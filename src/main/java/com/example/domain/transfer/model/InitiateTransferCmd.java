package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record InitiateTransferCmd(
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    // Included to satisfy the specific "Given" constraint in S-13
    BigDecimal availableBalance
) implements Command {}