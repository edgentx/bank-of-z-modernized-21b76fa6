package com.example.domain.userinterfacenavigation.command;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String aggregateId, String screenId, String deviceType) implements Command {
}
