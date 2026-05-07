package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, int timeoutMinutes) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
    }

    // Convenience method to get timestamp for the event
    public Instant occurredAt() {
        return Instant.now();
    }
}
