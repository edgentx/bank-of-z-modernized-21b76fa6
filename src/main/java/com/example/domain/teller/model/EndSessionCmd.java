package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active teller session.
 * Clears sensitive state and terminates the workflow.
 */
public record EndSessionCmd(String sessionId) implements Command {}
