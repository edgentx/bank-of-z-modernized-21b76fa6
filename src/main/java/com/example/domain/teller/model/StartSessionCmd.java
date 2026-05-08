package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Corresponds to Feature: S-18 StartSessionCmd.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
}