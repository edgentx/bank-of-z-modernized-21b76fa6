package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu or action.
 */
public record NavigateMenuCmd(
    String sessionId,
    String tellerId,
    String menuId,
    String action
) implements Command {}
