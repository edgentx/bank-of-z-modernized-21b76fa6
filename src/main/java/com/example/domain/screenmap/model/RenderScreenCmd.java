package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record RenderScreenCmd(String screenId, DeviceType deviceType) implements Command {
    public RenderScreenCmd {
        // Basic sanity validation, though business logic validation resides in the Aggregate
        Objects.requireNonNull(screenId, "screenId cannot be null in record construction");
    }
}
