package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }
}
