package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
