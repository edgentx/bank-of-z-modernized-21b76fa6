package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * Part of S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
