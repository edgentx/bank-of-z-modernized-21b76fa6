package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to end an existing TellerSession.
 */
public record EndSessionCmd(String sessionId, Instant endedAt) implements Command {}
