package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to instruct the TellerSession to route to a specific menu or screen.
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action
) implements Command {}
