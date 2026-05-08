package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Context: S-18 Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        long timeoutInSeconds,
        TellerSessionState navigationContext
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }

    // Convenience constructor for happy path defaults
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated) {
        this(sessionId, tellerId, terminalId, authenticated, 1800, new TellerSessionState("HOME", "DEFAULT"));
    }

}
