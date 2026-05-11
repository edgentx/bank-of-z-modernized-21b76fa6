package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a statement as a downloadable artifact (e.g., PDF).
 * Part of Story S-9: ExportStatementCmd.
 */
public record ExportStatementCmd(
    String statementId,
    String format // e.g., "PDF", "CSV"
) implements Command {
}
