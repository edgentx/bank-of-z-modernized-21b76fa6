package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to end a teller session.
 * S-20: Terminates the teller session and clears sensitive session state.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
    }
}
