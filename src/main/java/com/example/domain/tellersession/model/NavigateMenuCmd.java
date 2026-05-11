package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 */
public class NavigateMenuCmd implements Command {

    private final String sessionId;
    private final String menuId;
    private final String action;

    public NavigateMenuCmd(String sessionId, String menuId, String action) {
        this.sessionId = sessionId;
        this.menuId = menuId;
        this.action = action;
    }

    public String sessionId() {
        return sessionId;
    }

    public String menuId() {
        return menuId;
    }

    public String action() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavigateMenuCmd that = (NavigateMenuCmd) o;
        return Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(menuId, that.menuId) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, menuId, action);
    }
}
