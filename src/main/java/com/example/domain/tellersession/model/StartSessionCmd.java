package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Record carrying primitive values as per DDD conventions.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentNavState
) implements Command {}
