package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a Statement as a downloadable artifact.
 * S-9: Implement ExportStatementCmd.
 */
public record ExportStatementCmd(String statementId, String format) implements Command {
    if (statementId == null || statementId.isBlank()) {
        throw new IllegalArgumentException("statementId cannot be null or blank");
    }
    if (format == null || format.isBlank()) {
        throw new IllegalArgumentException("format cannot be null or blank");
    }
}
