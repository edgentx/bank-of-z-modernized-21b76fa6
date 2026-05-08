package com.example.domain.uimodel.command;

import com.example.domain.shared.Command;

import java.util.UUID;

/**
 * Command to initiate a new Teller Session.
 * <p>
 * This command is issued after the Teller successfully authenticates via the
 * Spring Security AuthZ pathway (CICS/IMS dual).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated // AuthZ Token validation result
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
    }
}
