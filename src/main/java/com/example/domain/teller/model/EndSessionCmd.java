package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * @param sessionId The unique identifier of the session to end.
 */
public record EndSessionCmd(String sessionId) implements Command {}
