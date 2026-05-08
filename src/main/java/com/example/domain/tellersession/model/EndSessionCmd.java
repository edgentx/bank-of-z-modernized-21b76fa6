package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active teller session.
 * Validates that the session belongs to the authenticated teller.
 */
public record EndSessionCmd(String sessionId, String authenticatedTellerId) implements Command {
}
