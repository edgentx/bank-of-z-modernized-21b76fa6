package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Context: S-18 (Teller Session)
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {

    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
    }

    // Lombok-style accessors not strictly needed for record, but useful for migration
    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
}
