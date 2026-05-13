package com.example.api.screenmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/** Operator-populated screen submitted back for validation + next-screen routing. */
public record ScreenInputRequest(
    @NotBlank String screenId,
    @NotNull Map<String, String> values) {}
