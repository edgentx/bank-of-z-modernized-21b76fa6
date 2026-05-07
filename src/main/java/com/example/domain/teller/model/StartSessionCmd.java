package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Validated and executed by the TellerSession aggregate.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    String navigationState
) implements Command {}
