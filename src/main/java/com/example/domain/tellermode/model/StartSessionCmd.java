package com.example.domain.tellermode.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
