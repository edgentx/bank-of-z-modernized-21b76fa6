package com.example.domain.tellerauthentication.model;

import com.example.domain.shared.Command;

public record AuthenticateTellerCmd(String authenticationId, String tellerId, String terminalId) implements Command {}
