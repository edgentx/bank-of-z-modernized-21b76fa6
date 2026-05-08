package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}