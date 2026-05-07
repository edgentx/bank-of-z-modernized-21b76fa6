package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * S-19: user-interface-navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId,
    String currentContext
) implements Command {}
