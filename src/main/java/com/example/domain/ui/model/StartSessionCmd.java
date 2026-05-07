package com.example.domain.ui.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Part of S-18: TellerSession user-interface-navigation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
