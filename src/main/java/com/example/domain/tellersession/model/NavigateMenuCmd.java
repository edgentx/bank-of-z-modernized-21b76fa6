package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu context.
 * Encapsulates the legacy 'screen' or 'menu' routing logic.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
