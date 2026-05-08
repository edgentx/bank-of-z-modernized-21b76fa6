package com.example.domain.tellermaster.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
