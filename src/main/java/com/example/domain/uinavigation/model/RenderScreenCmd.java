package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String screenId, DeviceType deviceType) implements Command {
}
