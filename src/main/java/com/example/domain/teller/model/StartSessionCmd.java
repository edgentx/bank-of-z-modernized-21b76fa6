package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * S-18
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated
) implements Command {}
