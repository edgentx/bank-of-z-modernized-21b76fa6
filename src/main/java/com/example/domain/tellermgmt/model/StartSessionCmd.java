package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
