package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
}
