package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Context: S-18 - TellerSession Aggregate
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, boolean isTimeoutConfigured, boolean isNavigationStateValid) implements Command {
}