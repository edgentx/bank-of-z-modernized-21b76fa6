package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu context.
 * S-19
 */
public record NavigateMenuCmd(
    String sessionId,
    String currentMenuId,
    String currentContext,
    String targetMenuId
) implements Command {}
