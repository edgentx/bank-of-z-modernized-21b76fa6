package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: Story S-18 (Teller Session Management).
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated, // Simplified auth flag for testing
    boolean isTimeoutConfigured
) implements Command {}
