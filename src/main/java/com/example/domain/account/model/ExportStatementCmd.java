package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to export a statement as a downloadable artifact.
 * Belongs to 'account-management' bounded context.
 */
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
