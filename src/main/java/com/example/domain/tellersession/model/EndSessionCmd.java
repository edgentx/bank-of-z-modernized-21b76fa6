package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * S-20: User-Interface-Navigation
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {}
