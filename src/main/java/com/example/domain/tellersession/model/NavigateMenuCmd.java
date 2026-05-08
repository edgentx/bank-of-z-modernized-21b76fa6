package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Part of the legacy 3270 emulator navigation flow.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}
