package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to export a statement to a specific format (e.g., PDF).
 * S-9.
 */
public record ExportStatementCmd(String statementId, String format) implements Command {
    public ExportStatementCmd {
        Objects.requireNonNull(statementId, "statementId cannot be null");
        Objects.requireNonNull(format, "format cannot be null");
        if (format.isBlank()) throw new IllegalArgumentException("format cannot be blank");
    }
}
