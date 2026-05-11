package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        int timeoutInSeconds,
        String navigationContext,
        Instant occurredAt
) implements Command {
}
