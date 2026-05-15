package com.example.api.terminal.dto;

import java.util.Map;

public record ScreenInputPayload(String screenId, Map<String, String> values) {}
