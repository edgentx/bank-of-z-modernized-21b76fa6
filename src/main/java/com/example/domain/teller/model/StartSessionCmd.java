package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Verifies authentication, terminal validity, and system state.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isSystemReady
) implements Command {}
