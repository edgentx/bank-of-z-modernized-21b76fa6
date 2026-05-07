package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Validates authentication, timeout status, and navigation context state.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTimedOut,
        boolean isNavStateInvalid
) implements Command {

    public StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) {
        this(tellerId, terminalId, isAuthenticated, false, false);
    }

    public StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated, boolean isTimedOut) {
        this(tellerId, terminalId, isAuthenticated, isTimedOut, false);
    }
}
