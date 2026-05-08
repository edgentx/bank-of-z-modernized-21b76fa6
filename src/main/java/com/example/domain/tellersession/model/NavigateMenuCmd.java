package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command for S-19: NavigateMenuCmd.
 * Instructs the TellerSession to route the teller to a specific menu context.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action
) implements Command {}
