package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
}