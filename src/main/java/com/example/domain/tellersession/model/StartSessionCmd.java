package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Wraps the ID of the teller and the terminal they are logging into.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentNavigationState,
    long lastActivityTimestamp
) implements Command {}
