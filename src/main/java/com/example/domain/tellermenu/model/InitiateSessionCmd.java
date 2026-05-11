package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;

public record InitiateSessionCmd(String sessionId, String tellerId, String menuId) implements Command {}
