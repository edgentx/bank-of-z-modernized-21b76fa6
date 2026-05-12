// 3270/TN3270 screen-map DTOs + client surface for the Bank-of-Z teller frontend.
//
// Shape mirrors what the Spring Boot teller-core service exposes for
// `ScreenMapAggregate` (RenderScreenCmd / ValidateScreenInputCmd, BANK S-21
// + S-22). The REST envelope on the backend is still being assembled; until
// a handler ships, this contract is the source of truth that any backend
// adapter must satisfy. Field names are camelCase to match Spring's default
// Jackson serialisation.

import { api } from './client';

export type ScreenHighlight = 'NORMAL' | 'BRIGHT' | 'REVERSE';

export interface ScreenField {
  /** Stable field key (matches BMS DFHMDF NAME=). */
  name: string;
  /** 1-indexed row (1..rows), matching BMS POS=(row,col) semantics. */
  row: number;
  /** 1-indexed column (1..cols). */
  col: number;
  /** Maximum input length (BMS LENGTH=). 0 for protected labels. */
  length: number;
  /** Pre-rendered label/static text for protected fields. */
  label?: string;
  /** Initial value for unprotected fields. */
  value?: string;
  /** True when the field is display-only (BMS ATTRB=PROT). */
  protected: boolean;
  /** Display attribute mapped from BMS HILIGHT=. */
  highlight: ScreenHighlight;
}

export interface ScreenMap {
  /** Stable screen identifier (e.g. "SIGNON", "MAINMENU"). */
  screenId: string;
  /** Human-readable title shown above the terminal grid. */
  title: string;
  /** Row count — almost always 24 for 3270 model 2. */
  rows: number;
  /** Column count — almost always 80 for 3270 model 2. */
  cols: number;
  /** Ordered field list. Order defines Tab traversal. */
  fields: ScreenField[];
}

export interface ScreenInputPayload {
  screenId: string;
  values: Record<string, string>;
}

export const terminalApi = {
  /** Fetch a screen map by its 3270 screen identifier. */
  getScreen: (screenId: string, signal?: AbortSignal): Promise<ScreenMap> =>
    api.get<ScreenMap>(`/terminal/screens/${encodeURIComponent(screenId)}`, { signal }),
  /** Submit a populated screen back to the backend for validation. */
  submit: (payload: ScreenInputPayload, signal?: AbortSignal): Promise<ScreenMap> =>
    api.post<ScreenMap, ScreenInputPayload>('/terminal/screens/submit', payload, { signal }),
};
