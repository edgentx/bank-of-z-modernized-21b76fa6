package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Carries authentication status, terminal identification, and navigation context.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String navigationState,
    Instant lastActivityTimestamp
) implements Command {}
