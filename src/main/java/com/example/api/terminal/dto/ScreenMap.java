package com.example.api.terminal.dto;

import java.util.List;

public record ScreenMap(
    String screenId,
    String title,
    int rows,
    int cols,
    List<ScreenField> fields) {}
