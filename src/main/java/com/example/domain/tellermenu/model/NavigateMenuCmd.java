package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String menuId, String action) implements Command {
}
