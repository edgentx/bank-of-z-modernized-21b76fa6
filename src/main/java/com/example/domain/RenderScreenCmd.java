package com.example.domain;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String aggregateId, String screenName, String deviceType) implements Command {}
