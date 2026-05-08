package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a Statement as a downloadable artifact (e.g., PDF).
 */
public record ExportStatementCmd(
    String statementId,
    String format
) implements Command {
}
