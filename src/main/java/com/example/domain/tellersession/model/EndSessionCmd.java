package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Validates sensitive data clearing and user context integrity.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
}