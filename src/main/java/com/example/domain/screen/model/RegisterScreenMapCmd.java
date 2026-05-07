package com.example.domain.screen.model;

import com.example.domain.shared.Command;

public record RegisterScreenMapCmd(
    String screenMapId, String mapName, int rows, int columns, String layoutSpec
) implements Command {}
