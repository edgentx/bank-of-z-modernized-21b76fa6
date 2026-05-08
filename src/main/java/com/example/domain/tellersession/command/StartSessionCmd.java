package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 * Consolidated canonical definition per S-18.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant occurredAt,
        boolean isTerminalInError // Used to simulate context error scenarios
) implements Command {}
