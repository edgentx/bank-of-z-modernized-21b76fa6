package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: S-18 TellerSession (user-interface-navigation).
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    String navigationState
) implements Command {
}