package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
    }
}
