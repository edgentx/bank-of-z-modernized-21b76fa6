package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Part of Story S-20.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {}
