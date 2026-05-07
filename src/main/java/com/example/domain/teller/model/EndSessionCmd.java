package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller Session.
 * Corresponds to Story S-20.
 */
public record EndSessionCmd(String sessionId) implements Command {}
