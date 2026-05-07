package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, String currentContext) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}
