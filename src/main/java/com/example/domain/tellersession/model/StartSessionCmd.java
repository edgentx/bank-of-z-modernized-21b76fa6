package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 * S-18: StartSessionCmd
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String navigationContext
) implements Command {}
