package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * Instructs the aggregate to clear sensitive state and record the termination event.
 */
public record EndSessionCmd(String sessionId) implements Command {}
