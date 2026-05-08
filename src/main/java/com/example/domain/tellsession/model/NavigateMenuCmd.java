package com.example.domain.tellsession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a specific menu.
 * Part of S-19 User Interface Navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
}
