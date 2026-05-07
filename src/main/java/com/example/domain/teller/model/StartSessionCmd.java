package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.UUID;

/**
 * Command to initiate a teller session.
 * Context: S-18 Teller Session Start
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        String authToken // Token or proof of authentication
) implements Command {
    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken cannot be null or blank");
        }
    }
}