package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Record carrying the necessary context to validate the invariants.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated
) implements Command {}
