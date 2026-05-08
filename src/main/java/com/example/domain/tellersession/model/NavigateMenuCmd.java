package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
}
