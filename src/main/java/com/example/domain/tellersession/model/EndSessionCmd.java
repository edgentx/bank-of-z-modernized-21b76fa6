package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active teller session.
 * Validates the session ID and ensures proper termination.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
