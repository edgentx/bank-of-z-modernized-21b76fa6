package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 */
public record StartSessionCmd(
    String aggregateId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    boolean isInvalidNavigationState
) implements Command {
    // Simple record to satisfy interface and carry state