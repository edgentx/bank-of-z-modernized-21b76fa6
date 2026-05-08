package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Used in: S-20 (user-interface-navigation)
 */
public record EndSessionCmd(String sessionId) implements Command {}
