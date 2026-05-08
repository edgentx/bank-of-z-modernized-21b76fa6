package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to start a new teller session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Boolean isAuthenticated,
        Instant timestamp
) implements Command {
}