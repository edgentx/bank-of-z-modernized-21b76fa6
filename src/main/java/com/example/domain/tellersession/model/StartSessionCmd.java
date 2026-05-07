package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Validations are handled by the TellerSession aggregate.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant authenticatedAt,
        String operationalContext
) implements Command {}
