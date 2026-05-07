package com.example.domain.tellermemory.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * @param sessionId The ID of the session to end.
 * @param reason The reason for termination (e.g., "User logout", "Timeout").
 */
public record EndSessionCmd(String sessionId, String reason) implements Command {}
