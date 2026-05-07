package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

import java.util.Objects;

public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant lastActivityAt,
        String navigationContext
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
        Objects.requireNonNull(lastActivityAt, "lastActivityAt required");
        Objects.requireNonNull(navigationContext, "navigationContext required");
    }
}
