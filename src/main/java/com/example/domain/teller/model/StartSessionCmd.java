package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * Acts as the DTO input for the aggregate.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String initialNavigationState,
    Instant requestTimestamp
) implements Command {}
