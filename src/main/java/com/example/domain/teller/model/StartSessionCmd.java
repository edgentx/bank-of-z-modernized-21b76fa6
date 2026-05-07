package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 * Validated and executed by TellerSessionAggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, String context) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
    }
}