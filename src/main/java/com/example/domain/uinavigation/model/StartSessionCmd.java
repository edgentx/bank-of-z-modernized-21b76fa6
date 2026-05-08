package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller session.
 * Requires valid Teller and Terminal IDs.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {}