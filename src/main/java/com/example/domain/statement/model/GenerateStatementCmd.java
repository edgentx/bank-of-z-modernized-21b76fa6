package com.example.domain.statement.model;
import com.example.domain.shared.Command;
import java.time.LocalDate;
public record GenerateStatementCmd(String accountNumber, LocalDate periodEnd) implements Command {}
