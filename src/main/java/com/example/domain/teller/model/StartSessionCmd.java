package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a teller session.
 * S-18 user-interface-navigation
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isNavigationValid,
        Instant lastActivityAt
) implements Command {
}