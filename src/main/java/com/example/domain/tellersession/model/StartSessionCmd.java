package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Verifies authentication and terminal availability.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, boolean isTerminalAvailable) implements Command {}
