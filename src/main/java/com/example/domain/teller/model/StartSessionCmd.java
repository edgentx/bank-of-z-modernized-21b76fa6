package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId, String authToken, String currentNavigationState) implements Command {}
