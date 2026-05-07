package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Context: User-Interface-Navigation (S-20).
 */
public record EndSessionCmd(String sessionId) implements Command {}
