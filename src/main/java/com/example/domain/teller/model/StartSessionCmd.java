package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Validations: Teller must be authenticated.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated, // Derived from AuthN service
    boolean isTerminalAvailable, // Derived from Terminal state
    long inactivityThresholdMs // Configured timeout
) implements Command {}
