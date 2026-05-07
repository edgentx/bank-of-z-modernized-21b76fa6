package com.example.domain.telllersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record InitiateTellerSessionCmd(String sessionId, String tellerId, String initialMenu, Instant sessionTimeout) implements Command {}
