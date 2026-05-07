package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record RenderScreenCmd(String screenId, String deviceType, int width, int height) implements Command {
    public RenderScreenCmd {
        Objects.requireNonNull(screenId, "screenId cannot be null"); // Basic defensive programming, though Aggregate validates invariants
    }
}
