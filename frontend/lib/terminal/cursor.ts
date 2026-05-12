// 3270 cursor / field-navigation helpers.
//
// 3270 navigation only moves between *unprotected* fields (the operator
// cannot park the cursor on a label). Tab wraps from the last unprotected
// field back to the first; Shift+Tab does the inverse. Field ordering is
// taken from the on-the-wire ScreenMap.fields array — which the backend
// emits in row-major / column-major order matching the legacy BMS map.

import type { ScreenField } from '@/lib/api/terminal';

/** Indexes (in `fields`) of every input-capable field, preserving order. */
export function unprotectedIndexes(fields: ReadonlyArray<ScreenField>): number[] {
  const out: number[] = [];
  for (let i = 0; i < fields.length; i++) {
    if (!fields[i].protected) out.push(i);
  }
  return out;
}

/** First navigable field, or `null` when the screen is read-only. */
export function firstFieldIndex(fields: ReadonlyArray<ScreenField>): number | null {
  const tabStops = unprotectedIndexes(fields);
  return tabStops.length === 0 ? null : tabStops[0];
}

/**
 * Tab to the next unprotected field, wrapping at end-of-screen.
 * Returns the same index when no unprotected field exists.
 */
export function nextFieldIndex(
  fields: ReadonlyArray<ScreenField>,
  current: number | null,
): number | null {
  const tabStops = unprotectedIndexes(fields);
  if (tabStops.length === 0) return null;
  if (current === null) return tabStops[0];
  const pos = tabStops.indexOf(current);
  if (pos === -1) return tabStops[0];
  return tabStops[(pos + 1) % tabStops.length];
}

/**
 * Shift+Tab to the previous unprotected field, wrapping at start-of-screen.
 */
export function prevFieldIndex(
  fields: ReadonlyArray<ScreenField>,
  current: number | null,
): number | null {
  const tabStops = unprotectedIndexes(fields);
  if (tabStops.length === 0) return null;
  if (current === null) return tabStops[tabStops.length - 1];
  const pos = tabStops.indexOf(current);
  if (pos === -1) return tabStops[tabStops.length - 1];
  return tabStops[(pos - 1 + tabStops.length) % tabStops.length];
}

/**
 * Convert a 1-indexed (row, col) pair into the linear cell offset used by
 * CSS grid placement. Out-of-bounds inputs clamp to the grid extent so a
 * malformed payload from the host can still render.
 */
export function cellOffset(row: number, col: number, cols: number): number {
  const r = Math.max(1, row);
  const c = Math.max(1, Math.min(cols, col));
  return (r - 1) * cols + (c - 1);
}
