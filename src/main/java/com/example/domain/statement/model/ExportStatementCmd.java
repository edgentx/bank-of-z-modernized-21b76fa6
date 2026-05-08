package com.example.domain.statement.model;

import com.example.domain.shared.Command;

public record ExportStatementCmd(String statementId, String format, String previousStatementClosingBalance) implements Command {}
