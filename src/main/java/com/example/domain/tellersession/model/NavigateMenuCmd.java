package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller session to a specific menu context.
 */
public record NavigateMenuCmd(String sessionId, String tellerId, String menuId, String action) implements Command {
}