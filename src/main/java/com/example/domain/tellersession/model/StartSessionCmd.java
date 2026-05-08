package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Validated by TellerSession aggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // Note: Validation of tellerId/terminalId content is delegated to the aggregate logic
        // or handled by the repository interface constraints. Here we ensure structural integrity.
    }
}
