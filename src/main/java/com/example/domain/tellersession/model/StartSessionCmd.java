package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Record carrying validated request fields.
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
        if (timeoutInSeconds <= 0) throw new IllegalArgumentException("timeoutInSeconds must be positive");
        Objects.requireNonNull(navigationContext, "navigationContext cannot be null");
    }
}