package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to terminate an active TellerSession.
 * Enforces invariants: AuthZ status, Timeout, and Nav-state consistency.
 */
public record EndSessionCmd(UUID sessionId, String tellerId) implements Command {}
