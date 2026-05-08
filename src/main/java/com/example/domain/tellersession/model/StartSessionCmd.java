package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Validates that the user is authenticated and the terminal is valid.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {
}