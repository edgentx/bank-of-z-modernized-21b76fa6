package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Records the necessary data to start the UI navigation context.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isContextValid,
    boolean isTimedOut
) implements Command {}
