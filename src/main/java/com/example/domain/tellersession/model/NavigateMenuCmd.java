package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * Part of S-19 user-interface-navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action // e.g., Enter, F3, PF1
) implements Command {}
