package com.example.domain.statement.model;

import com.example.domain.shared.Command;

import java.util.UUID;

public record ExportStatementCmd(String statementId, String format) implements Command {
    public ExportStatementCmd {
        if (statementId == null || statementId.isBlank()) {
            throw new IllegalArgumentException("statementId cannot be null or blank");
        }
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("format cannot be null or blank");
        }
    }
}