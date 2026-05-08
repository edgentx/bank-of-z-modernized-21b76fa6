package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a Teller Session.
 * Immutable record carrying the necessary context.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant timestamp,
        String initialNavigationState
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
    }

    // Constructor overload for simpler calls if default timestamp/nav is needed
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, Instant timestamp) {
        this(sessionId, tellerId, terminalId, isAuthenticated, timestamp, "HOME");
    }
}
