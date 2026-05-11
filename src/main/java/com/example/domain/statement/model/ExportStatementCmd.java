package com.example.domain.statement.model;

import com.example.domain.shared.Command;

public record ExportStatementCmd(
    String statementId,
    String format // e.g. "PDF", "CSV"
) implements Command {}
