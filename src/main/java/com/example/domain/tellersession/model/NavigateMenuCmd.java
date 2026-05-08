package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
