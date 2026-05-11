package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a statement in a specific format (e.g., PDF).
 * S-9
 */
public record ExportStatementCmd(String statementId, String format) implements Command {}
