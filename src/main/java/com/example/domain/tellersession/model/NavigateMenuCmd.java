package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public class NavigateMenuCmd implements Command {
    private final String sessionId;
    private final String menuId;
    private final String action;

    public NavigateMenuCmd(String sessionId, String menuId, String action) {
        this.sessionId = sessionId;
        this.menuId = menuId;
        this.action = action;
    }

    public String sessionId() { return sessionId; }
    public String menuId() { return menuId; }
    public String action() { return action; }
}
