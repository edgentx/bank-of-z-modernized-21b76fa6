package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Story S-18
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
