package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start a teller session.
 * S-18
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        String navigationState,
        Instant lastActivityAt
) implements Command {
}