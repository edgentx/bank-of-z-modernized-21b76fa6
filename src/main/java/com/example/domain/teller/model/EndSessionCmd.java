package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Validates that the session exists and can be terminated.
 */
public record EndSessionCmd(String sessionId) implements Command {}
