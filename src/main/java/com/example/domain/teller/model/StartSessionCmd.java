package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: User-Interface-Navigation (S-18).
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    long inactiveMillis // Used to test timeout invariants
) implements Command {}
