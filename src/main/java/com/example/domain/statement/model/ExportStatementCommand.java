package com.example.domain.statement.model;

import com.example.domain.shared.Command;

public record ExportStatementCommand(String statementId, String format) implements Command {}
