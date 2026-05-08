package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;
import java.util.Set;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> permissions
) implements Command {}
