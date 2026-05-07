package com.example.domain.tellermode.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String timeoutOverride,
    String mode,
    String navigationContext
) implements Command {}
