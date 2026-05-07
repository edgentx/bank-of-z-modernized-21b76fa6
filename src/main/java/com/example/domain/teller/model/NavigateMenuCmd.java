package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * Context: S-19 TellerSession Navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action
) implements Command {}
