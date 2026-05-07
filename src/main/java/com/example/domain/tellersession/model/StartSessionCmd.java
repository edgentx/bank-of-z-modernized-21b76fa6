package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant sessionTimeoutAt,
        boolean isAuthenticated,
        String expectedCurrentState,
        String actualCurrentState
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(sessionTimeoutAt);
    }

    // Constructor for standard usage where state validation isn't the primary driver
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant sessionTimeoutAt, boolean isAuthenticated) {
        this(sessionId, tellerId, terminalId, sessionTimeoutAt, isAuthenticated, null, null);
    }
}
