package com.example.domain.uimodel.command;

import java.util.Objects;

/**
 * Value object representing the UI Navigation State/Context.
 */
public class NavContext {
    private final String state;
    private final String screen;

    public NavContext(String state, String screen) {
        this.state = state;
        this.screen = screen;
    }

    public boolean isValid() {
        return state != null && !state.isBlank() && screen != null && !screen.isBlank();
    }

    @Override
    public String toString() {
        return "NavContext{state='" + state + "', screen='" + screen + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavContext navContext = (NavContext) o;
        return Objects.equals(state, navContext.state) && Objects.equals(screen, navContext.screen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, screen);
    }
}