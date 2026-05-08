package com.example.domain.tellermode.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * NB: This follows the naming pattern StartSessionCmd as requested.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated // Explicit flag derived from AuthZ context
) implements Command {}
