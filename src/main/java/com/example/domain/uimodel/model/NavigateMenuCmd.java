package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}