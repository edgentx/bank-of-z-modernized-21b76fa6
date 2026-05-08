package com.example.domain.ui.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Includes validation flags for specific environment/context invariants.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId
) implements Command {

    // Defaults for standard happy path execution
    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, true, true);
    }

    // Constructor allowing simulation of invariant violations for testing
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean timeoutConfigValid) {
        this(sessionId, tellerId, terminalId, timeoutConfigValid, true);
    }

    // Full constructor for all fields
    public StartSessionCmd(
            String sessionId,
            String tellerId,
            String terminalId,
            boolean timeoutConfigValid,
            boolean navStateValid
    ) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.timeoutConfigValid = timeoutConfigValid;
        this.navStateValid = navStateValid;
    }

    private final boolean timeoutConfigValid;
    private final boolean navStateValid;

    public boolean isTimeoutConfigValid() {
        return timeoutConfigValid;
    }

    public boolean isNavStateValid() {
        return navStateValid;
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
}