package com.example.domain.statement.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record ExportStatementCmd(String statementId, String format) implements Command {
    public ExportStatementCmd {
        Objects.requireNonNull(statementId, "statementId cannot be null");
        Objects.requireNonNull(format, "format cannot be null");
    }
}
