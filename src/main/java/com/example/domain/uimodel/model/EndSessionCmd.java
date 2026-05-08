package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller session.
 * Corresponds to Story S-20.
 */
public record EndSessionCmd(String sessionId) implements Command {}
