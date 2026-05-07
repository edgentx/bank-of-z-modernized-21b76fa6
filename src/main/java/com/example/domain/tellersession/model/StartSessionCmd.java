package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Preconditions: Teller must be authenticated, Terminal must be available.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated
) implements Command {}
