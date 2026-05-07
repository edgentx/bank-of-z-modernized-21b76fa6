package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu or action.
 * Part of the User-Interface-Navigation bounded context.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
