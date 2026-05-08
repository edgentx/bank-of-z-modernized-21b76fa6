package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 * @param tellerId The ID of the teller.
 * @param terminalId The ID of the terminal.
 * @param isAuthenticated Authentication status.
 * @param initialContext The starting navigation context.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String initialContext
) implements Command {}