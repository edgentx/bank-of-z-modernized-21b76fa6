package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Immutable record carrying the necessary authentication and context data.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    long inactivityTimeoutMillis,
    String operationalContext
) implements Command {}