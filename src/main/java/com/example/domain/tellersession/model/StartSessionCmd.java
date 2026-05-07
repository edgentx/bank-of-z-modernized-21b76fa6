package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Assumes authentication has occurred at the network/gateway layer, 
 * but the aggregate enforces that the command carries a valid authentication token/assertion.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isWithinTimeout,
    boolean isNavigationValid
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }

    // Convenience constructor for the happy path
    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, true, true, true);
    }
}