package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a teller session following successful authentication.
 * Accepts primitive types and value objects to avoid coupling to client DTOs.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant timestamp,
        String initialContext
) implements Command {
}
