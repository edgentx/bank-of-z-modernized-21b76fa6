package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String targetMenu) implements Command {
    public NavigateMenuCmd {
        if (targetMenu == null || targetMenu.isBlank()) {
            throw new IllegalArgumentException("targetMenu cannot be null or blank");
        }
    }
}
