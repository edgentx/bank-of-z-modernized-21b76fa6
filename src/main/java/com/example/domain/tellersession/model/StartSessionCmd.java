package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Part of S-18: TellerSession user-interface-navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}