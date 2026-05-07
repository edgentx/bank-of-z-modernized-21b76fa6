package com.example.domain;

import com.example.domain.shared.Command;

public record ReverseTransactionCmd(
    String transactionId,
    String originalTransactionId,
    double amount,
    String accountNo,
    boolean posted
) implements Command {}
