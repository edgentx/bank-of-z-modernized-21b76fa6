package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        Instant periodEnd,
        BigDecimal openingBalance,
        Optional<BigDecimal> previousClosingBalance // Optional context for validation
) implements Command {}
