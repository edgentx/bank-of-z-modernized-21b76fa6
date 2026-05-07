package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Story: S-18
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String operationalContext
) implements Command {}
