package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * Immutable record carrying the required data.
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated, boolean isActive, String contextState) implements Command {
}
