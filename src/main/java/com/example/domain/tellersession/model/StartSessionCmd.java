package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * Carries the authentication status, teller ID, and terminal ID.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {
}
