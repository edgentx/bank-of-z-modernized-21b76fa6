package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Requires authentication context (tellerId) and terminal context.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Duration timeoutDuration,
        TellerSessionState initialState
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        // Validation for business rules is handled in the Aggregate,
        // but basic structural sanity checks are good practice here.
    }

    // Factory for test convenience often used in Step Definitions
    public StartSessionCmd(String tellerId, String terminalId, Duration timeoutDuration, TellerSessionState initialState) {
        this("dummy-session-id", tellerId, terminalId, timeoutDuration, initialState);
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
    public Duration timeoutDuration() { return timeoutDuration; }
    public TellerSessionState initialState() { return initialState; }
}
