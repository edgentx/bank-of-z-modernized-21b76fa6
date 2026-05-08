package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Invariants: Authentication status, Timeout validity, Context integrity.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
