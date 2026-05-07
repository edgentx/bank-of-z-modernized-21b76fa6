package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * S-18: StartSessionCmd
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Instant lastActivityAt,
    String navigationState
) implements Command {}
