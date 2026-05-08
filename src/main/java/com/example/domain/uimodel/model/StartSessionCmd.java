package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session on a specific terminal.
 * Preconditions: Teller is authenticated against the host system.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentNavigationState
) implements Command {}
