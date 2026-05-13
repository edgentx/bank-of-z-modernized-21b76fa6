package com.example.api.screenmap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One 3270 screen field (BMS DFHMDF). Shape mirrors the frontend's
 * {@code lib/api/terminal.ts#ScreenField}. Row/col are 1-indexed.
 *
 * <p>The Java component name is {@code isProtected} because {@code protected}
 * is a reserved word; {@code @JsonProperty} renames it on the wire so the
 * frontend deserialiser sees the canonical 3270 attribute name.
 */
public record ScreenFieldDto(
    String name,
    int row,
    int col,
    int length,
    String label,
    String value,
    @JsonProperty("protected") boolean isProtected,
    String highlight) {

  public static ScreenFieldDto label(String name, int row, int col, String text) {
    return new ScreenFieldDto(name, row, col, 0, text, null, true, "NORMAL");
  }

  public static ScreenFieldDto bright(String name, int row, int col, String text) {
    return new ScreenFieldDto(name, row, col, 0, text, null, true, "BRIGHT");
  }

  public static ScreenFieldDto input(String name, int row, int col, int length) {
    return new ScreenFieldDto(name, row, col, length, null, "", false, "NORMAL");
  }
}
