package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a new Teller UI session.
 * S-18
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        String operationalContext,
        Instant timestamp
) implements Command {
}
