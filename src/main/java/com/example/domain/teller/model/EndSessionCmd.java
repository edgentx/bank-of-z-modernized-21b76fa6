package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the teller session and clear sensitive state.
 */
public record EndSessionCmd(String sessionId) implements Command {}
