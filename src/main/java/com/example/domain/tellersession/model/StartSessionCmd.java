package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Used after successful authentication (Story S-18).
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean validTerminalContext
) implements Command {

    public boolean isValidTerminalContext() {
        return validTerminalContext;
    }
}