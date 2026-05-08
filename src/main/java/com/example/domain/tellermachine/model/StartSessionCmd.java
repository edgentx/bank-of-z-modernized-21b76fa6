package com.example.domain.tellermachine.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Part of Story S-18.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    int timeoutSeconds,
    String initialContext
) implements Command {
}
