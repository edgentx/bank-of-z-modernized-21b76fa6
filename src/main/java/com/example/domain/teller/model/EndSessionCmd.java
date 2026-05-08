package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * Part of User-Interface-Navigation context (S-20).
 */
public record EndSessionCmd(String sessionId) implements Command {}
