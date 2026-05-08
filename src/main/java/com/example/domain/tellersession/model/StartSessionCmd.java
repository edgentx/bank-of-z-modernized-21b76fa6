package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    long timeoutMillis,
    String navState
) implements Command {

    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
    }
}