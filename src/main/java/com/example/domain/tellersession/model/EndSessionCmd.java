package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Used by S-20: EndSessionCmd
 */
public record EndSessionCmd(String sessionId) implements Command {}
