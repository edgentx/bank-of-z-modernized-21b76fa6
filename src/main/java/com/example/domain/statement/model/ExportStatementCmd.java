package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a Statement.
 */
public record ExportStatementCmd(String statementId, String format) implements Command {
    public ExportStatementCmd {
        if (statementId == null || statementId.isBlank()) {
            throw new IllegalArgumentException("statementId required");
        }
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("format required");
        }
    }
}
