package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record EvaluateRoutingCmd(String routeId, int rulesVersion, Map<String, String> payload) implements Command {}
