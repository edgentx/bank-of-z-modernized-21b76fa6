package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Used to clear sensitive state and logout the teller.
 */
public record EndSessionCmd(String sessionId) implements Command {}
