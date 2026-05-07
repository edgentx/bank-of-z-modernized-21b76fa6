package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 * S-18: user-interface-navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
