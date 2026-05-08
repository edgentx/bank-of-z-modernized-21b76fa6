package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Set;

public record StartSessionCmd(
    String tellerId,
    String terminalId,
    Set<String> roles
) implements Command {}