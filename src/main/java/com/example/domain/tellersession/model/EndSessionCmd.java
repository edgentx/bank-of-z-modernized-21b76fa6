package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Clears sensitive state and emits SessionEndedEvent upon success.
 */
public record EndSessionCmd(String sessionId) implements Command {}
