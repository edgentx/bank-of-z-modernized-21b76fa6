package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a teller session.
 * Used by S-18 StartSessionCmd feature.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isNavigationContextValid,
    Instant commandTimestamp
) implements Command {}
