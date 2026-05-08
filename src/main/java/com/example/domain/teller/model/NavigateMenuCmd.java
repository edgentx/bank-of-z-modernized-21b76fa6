package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate to a specific menu or screen.
 * Used in Story S-19: TellerSession Navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
