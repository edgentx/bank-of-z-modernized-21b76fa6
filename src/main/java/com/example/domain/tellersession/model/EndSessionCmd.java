package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active teller session.
 * Story S-20: user-interface-navigation.
 */
public record EndSessionCmd(String sessionId) implements Command {}
