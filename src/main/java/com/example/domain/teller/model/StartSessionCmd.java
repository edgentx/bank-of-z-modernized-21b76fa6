package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Duration;
import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * Encapsulates the teller ID, terminal ID, session timeout configuration,
 * and the authentication status.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Duration timeout,
    Instant authenticatedAt
) implements Command {}
