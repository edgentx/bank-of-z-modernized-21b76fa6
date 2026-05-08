package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String sessionId, String terminalId) implements Command {
}
