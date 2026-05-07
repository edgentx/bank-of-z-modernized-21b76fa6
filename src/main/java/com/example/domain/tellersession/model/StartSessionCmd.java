package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}