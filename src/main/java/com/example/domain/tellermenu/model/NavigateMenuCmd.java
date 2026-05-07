package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu/screen.
 */
public record NavigateMenuCmd(String menuId, String action) implements Command {
}
