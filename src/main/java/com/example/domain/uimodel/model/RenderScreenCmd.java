package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record RenderScreenCmd(String mapId, String screenId, String deviceType) implements Command {
    public RenderScreenCmd {
        Objects.requireNonNull(mapId, "mapId cannot be null");
    }
}