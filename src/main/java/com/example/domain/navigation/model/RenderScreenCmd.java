package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to trigger the rendering of a specific screen.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
    public RenderScreenCmd {
        Objects.requireNonNull(screenId, "screenId cannot be null");
        Objects.requireNonNull(deviceType, "deviceType cannot be null");
    }
}