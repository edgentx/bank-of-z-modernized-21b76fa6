package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to instruct the Teller Terminal UI to navigate to a specific menu context.
 * S-19: Navigation command.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
