package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for S-19: NavigateMenuCmd.
 * Instructs the TellerSession aggregate to transition to a specific menu context.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action
) implements Command {}
