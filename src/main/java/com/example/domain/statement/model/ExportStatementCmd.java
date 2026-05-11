package com.example.domain.statement.model;

import com.example.domain.shared.Command;

/**
 * Command to export a Statement (e.g. to PDF).
 * Used in Story S-9.
 */
public record ExportStatementCmd(
    String statementId,
    String format // e.g. "PDF"
) implements Command {}
