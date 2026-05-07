package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
}
