package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 * Part of Story S-18: TellerSession UI Navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
