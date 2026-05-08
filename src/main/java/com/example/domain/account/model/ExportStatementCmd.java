package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record ExportStatementCmd(String statementId, String format) implements Command {}