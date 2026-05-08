package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Context: BANK S-18 - Teller Session Initiation.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated, // Authentication status from upstream auth service
    boolean isTerminalAvailable, // Operational context check
    boolean isWithinSessionTimeout // Inactivity check
) implements Command {}
