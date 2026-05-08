package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export an existing statement to a downloadable artifact (e.g., PDF).
 */
public record ExportStatementCmd(String statementId, String format) implements Command {
}
