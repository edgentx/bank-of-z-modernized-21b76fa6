package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record InitiateTellerSessionCmd(String sessionId) implements Command {}
