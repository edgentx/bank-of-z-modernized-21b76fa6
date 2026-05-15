package com.example.api.terminal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScreenField(
    String name,
    int row,
    int col,
    int length,
    String label,
    String value,
    @JsonProperty("protected") boolean isProtected,
    String highlight) {}
