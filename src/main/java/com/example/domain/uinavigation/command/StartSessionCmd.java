package com.example.domain.uinavigation.command;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated, boolean contextInvalid) implements Command {
}
