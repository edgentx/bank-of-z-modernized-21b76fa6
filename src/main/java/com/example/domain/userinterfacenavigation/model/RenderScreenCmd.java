package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record RenderScreenCmd(String aggregateId, String screenId, String deviceType, Map<String, Object> context) implements Command {}
