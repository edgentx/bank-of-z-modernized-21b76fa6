package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to end an existing teller session.
 * Encapsulates the session ID to be terminated.
 */
public record EndSessionCmd(UUID sessionId) implements Command {}
