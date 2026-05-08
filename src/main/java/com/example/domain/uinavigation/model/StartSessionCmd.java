package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Validates that the teller is authenticated and the terminal context is valid.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String navigationState
) implements Command {}
