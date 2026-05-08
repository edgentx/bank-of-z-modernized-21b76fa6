package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to terminate a teller session.
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {}
