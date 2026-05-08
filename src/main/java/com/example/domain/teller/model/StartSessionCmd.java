package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.UUID;

public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTimedOut,
        boolean isNavigationStateValid
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
