package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null/empty");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be null/empty");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be null/empty");
    }
}
