package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    }
}