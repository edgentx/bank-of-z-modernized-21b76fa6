package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to simulate navigation state change (Screen entry).
 * Used to setup invalid states for EndSessionCmd testing.
 */
public record EnterScreenCmd(String sessionId, String screenId) implements Command {}
