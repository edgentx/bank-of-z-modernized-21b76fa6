package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu.
 * Part of Feature S-19.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
}