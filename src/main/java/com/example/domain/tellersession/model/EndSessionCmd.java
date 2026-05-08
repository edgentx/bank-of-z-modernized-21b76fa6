package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Borrowed from S-20 user-interface-navigation story.
 */
public record EndSessionCmd(String sessionId) implements Command {}
