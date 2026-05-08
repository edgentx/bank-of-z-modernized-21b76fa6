package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Immutable record (Java 21+).
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId
) implements Command {
}