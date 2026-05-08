package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Duration;

/**
 * Command to initiate a new teller session.
 * Context: User Interface Navigation (S-18).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Duration timeoutDuration,
        boolean isAuthenticated,
        boolean isNavigationContextValid
) implements Command {
}