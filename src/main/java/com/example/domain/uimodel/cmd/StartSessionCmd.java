package com.example.domain.uimodel.cmd;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * Maps to S-18 Acceptance Criteria.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String targetContext, // Operational context/Screen ID
    boolean isAuthenticated, // Pre-condition flag
    Instant occurredAt // Timestamp for timeout validation
) implements Command {}
