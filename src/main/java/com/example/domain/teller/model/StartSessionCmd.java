package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Context: User-Interface-Navigation (S-18).
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    long currentTimestamp,
    String navigationState
) implements Command {}
