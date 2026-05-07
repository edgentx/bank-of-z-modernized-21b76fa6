package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String menuId, String action) implements Command {}
