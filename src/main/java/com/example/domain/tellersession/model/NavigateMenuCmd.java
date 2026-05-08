package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Encapsulates the intent to move between operational contexts in the legacy system emulation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
