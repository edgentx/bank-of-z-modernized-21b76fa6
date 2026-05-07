package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId, String authToken) implements Command {
    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}
