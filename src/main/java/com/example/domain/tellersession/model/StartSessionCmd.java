package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated, String sessionState) implements Command {
}
