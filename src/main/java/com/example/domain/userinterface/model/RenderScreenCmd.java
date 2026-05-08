package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to render a specific screen layout for a target device.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {

    public RenderScreenCmd {
        Objects.requireNonNull(screenMapId, "screenMapId cannot be null");
        // screenId and deviceType validated in the aggregate, but basic record validation here is good practice.
    }
}
