package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Contract enforces authentication, timeout configuration, and navigation state.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        int timeoutInSeconds,
        String navigationContext
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(navigationContext, "navigationContext cannot be null");
    }

    // Expose the required validation methods expected by the aggregate logic
    // This satisfies the compiler errors looking for isAuthenticated(), etc.
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public int timeoutInSeconds() {
        return timeoutInSeconds;
    }

    public String navigationContext() {
        return navigationContext;
    }
}
