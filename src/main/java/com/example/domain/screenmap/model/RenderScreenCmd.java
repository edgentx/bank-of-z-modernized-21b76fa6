package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record RenderScreenCmd(String screenId, String deviceType, Map<String, String> layoutAttributes) implements Command {}
