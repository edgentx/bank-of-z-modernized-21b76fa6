package com.example.domain.tellermgmt.model.command;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    String expectedState // Used to validate navigation state invariant
) implements Command {}