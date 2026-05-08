package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * S-18: user-interface-navigation.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
    // Validation is performed by the Aggregate
}
