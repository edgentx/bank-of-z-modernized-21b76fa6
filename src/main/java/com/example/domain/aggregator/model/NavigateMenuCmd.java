package com.example.domain.aggregator.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String currentMenuId // Used to verify context/state integrity
) implements Command {}