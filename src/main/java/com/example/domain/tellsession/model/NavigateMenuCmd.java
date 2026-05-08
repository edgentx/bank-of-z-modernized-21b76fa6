package com.example.domain.tellsession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu context.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
