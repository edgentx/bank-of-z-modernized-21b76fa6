package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Duration;

/**
 * Command to initiate a new teller session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        String authToken,
        Duration timeout,
        String context
) implements Command {

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, String authToken) {
        this(sessionId, tellerId, terminalId, authToken, Duration.ofMinutes(30), "DEFAULT_CONTEXT");
    }

    public StartSessionCmd(String sessionId, String tellerId, String terminalId, String authToken, Duration timeout) {
        this(sessionId, tellerId, terminalId, authToken, timeout, "DEFAULT_CONTEXT");
    }
}