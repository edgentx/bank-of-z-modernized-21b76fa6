package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a new Teller Session.
 * corresponds to S-18 StartSessionCmd.
 */
public record StartSessionCmd(
        String aggregateId,
        String terminalId,
        Instant sessionTimeoutAt,
        String expectedNavigationState,
        boolean isAuthenticated // S-18 Requirement: A teller must be authenticated
) implements Command {}
