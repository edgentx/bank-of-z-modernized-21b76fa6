package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to end a teller session.
 * Context: Story S-20 (user-interface-navigation).
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {}
