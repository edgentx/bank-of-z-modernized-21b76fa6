package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 * Context: S-18 Teller Session Initialization
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant authenticatedAt) implements Command {
}