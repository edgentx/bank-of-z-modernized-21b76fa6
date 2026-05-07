package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * Encapsulates authentication status, terminal details, and state flags for invariant validation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    boolean isValidNavigationState
) implements Command {
}
