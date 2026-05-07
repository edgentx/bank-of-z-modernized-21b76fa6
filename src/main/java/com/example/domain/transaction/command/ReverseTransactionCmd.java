package com.example.domain.transaction.command;

import com.example.domain.shared.Command;

public record ReverseTransactionCmd(String transactionId, String originalTransactionId) implements Command {}
