package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
