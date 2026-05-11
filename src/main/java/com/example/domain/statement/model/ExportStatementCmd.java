package com.example.domain.statement.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record ExportStatementCmd(
    String statementId,
    String format,
    BigDecimal previousClosingBalance,
    boolean isPeriodClosed
) implements Command {}
