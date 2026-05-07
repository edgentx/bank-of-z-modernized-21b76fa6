package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Clearing sensitive state and terminating the workflow.
 */
public record EndSessionCmd(String sessionId) implements Command {
}