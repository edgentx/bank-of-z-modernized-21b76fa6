package com.example.domain.ui.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record RenderScreenCmd(String screenId, String deviceType) implements Command {
    public RenderScreenCmd {
        Objects.requireNonNull(screenId, "screenId cannot be null");
        Objects.requireNonNull(deviceType, "deviceType cannot be null");
    }
}
