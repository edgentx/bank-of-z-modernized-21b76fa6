package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Set;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> roles,
    String operationalContext
) implements Command {}
