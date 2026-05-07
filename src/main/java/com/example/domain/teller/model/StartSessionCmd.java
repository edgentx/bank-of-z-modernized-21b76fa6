package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Carries state for TDD validation (authentication, timeout, navigation).
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTimedOut,
        boolean isNavValid
) implements Command {

    // Convenience constructors for valid/default scenarios
    public StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) {
        this(tellerId, terminalId, isAuthenticated, false, true);
    }
}