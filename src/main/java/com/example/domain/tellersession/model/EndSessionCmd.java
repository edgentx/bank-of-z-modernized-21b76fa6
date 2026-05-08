package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Used in S-20: EndSessionCmd.
 */
public record EndSessionCmd(String sessionId) implements Command {}
