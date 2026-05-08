package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Duration;

/**
 * Command to initiate a teller session following successful authentication.
 * Context: S-18 user-interface-navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Duration timeout, String navigationState) implements Command {
}
