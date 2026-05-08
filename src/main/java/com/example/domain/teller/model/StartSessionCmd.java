package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Immutable record carrying authentication and terminal context.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {
}
