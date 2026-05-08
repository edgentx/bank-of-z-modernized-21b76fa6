package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Part of Story S-18: TellerSession user-interface-navigation.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId
) implements Command {}
