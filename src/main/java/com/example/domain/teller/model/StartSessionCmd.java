package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.time.Instant;

/**
 * Command to initiate a new Teller Session.
 * Carries necessary authentication, context, and configuration data.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Duration timeoutDuration,
    Instant occurredAt,
    String authToken,
    String initialNavState
) implements Command {

    // Constructor for simplified valid scenarios (defaults)
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, Duration timeoutDuration, Instant occurredAt) {
        this(sessionId, tellerId, terminalId, timeoutDuration, occurredAt, "valid-auth-token", "HOME_SCREEN");
    }
}
