package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record RenderScreenCmd(String screenId, DeviceType deviceType, Map<String, Object> layoutContext) implements Command {
}
