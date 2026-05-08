package com.example.domain.ui.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) implements Command {}
