package com.example.domain.transaction;

import com.example.domain.shared.Command;

public record ReverseTransactionCmd(String originalTransactionId, String amount) implements Command {}
