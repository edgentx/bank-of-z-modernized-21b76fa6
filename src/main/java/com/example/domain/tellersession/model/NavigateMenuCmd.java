package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu.
 * Part of Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String targetMenu, String action) implements Command {
}
