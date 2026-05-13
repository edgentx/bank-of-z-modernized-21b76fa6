package com.example.api.screenmap.dto;

import java.util.List;

/**
 * A 3270 screen map — equivalent to a BMS MAP / DFHMDF definition rendered
 * for the teller terminal. Shape mirrors the frontend's
 * {@code lib/api/terminal.ts#ScreenMap}.
 */
public record ScreenMapResponse(
    String screenId,
    String title,
    int rows,
    int cols,
    List<ScreenFieldDto> fields) {

  public static final int DEFAULT_ROWS = 24;
  public static final int DEFAULT_COLS = 80;
}
