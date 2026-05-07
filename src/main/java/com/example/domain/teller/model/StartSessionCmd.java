package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to initiate a teller session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, String currentNavState) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }
}