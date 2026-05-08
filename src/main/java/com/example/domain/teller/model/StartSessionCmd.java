package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Encapsulates authentication status, terminal details, and context validity flags.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTimedOut,
        boolean isNavigationStateValid
) implements Command {}
