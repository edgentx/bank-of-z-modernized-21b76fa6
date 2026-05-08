package com.example.domain.aggregator.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String commandId,
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
