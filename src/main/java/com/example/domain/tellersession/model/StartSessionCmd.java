package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Duration;
import java.util.Objects;

/**
 * Command to initiate a teller session on a specific terminal.
 * Batch ID and AuthZ context are implicitly passed via the session context in a real handler,
 * but included here for aggregate invariant enforcement.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Duration configuredTimeout,
    boolean isNavigationStateValid
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(configuredTimeout, "configuredTimeout cannot be null");
    }
}
