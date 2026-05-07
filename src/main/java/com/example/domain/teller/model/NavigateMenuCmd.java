package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Part of S-19: NavigateMenuCmd.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action
) implements Command {}
