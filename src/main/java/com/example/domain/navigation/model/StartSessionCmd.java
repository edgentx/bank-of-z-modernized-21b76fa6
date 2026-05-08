package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be null/blank");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be null/blank");
    }
}