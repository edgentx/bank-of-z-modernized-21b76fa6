package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, boolean isActiveSession) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }
}
