/** Screen field definition — maps to a 3270 BMS field. */
export interface ScreenField {
  row: number;
  col: number;
  label?: string;
  name: string;
  type: "input" | "output" | "hidden";
  length: number;
  format?: "text" | "currency" | "date" | "number";
  bright?: boolean;
  protected?: boolean;
}

/** Maps a function key to an action. */
export interface FunctionKeyMapping {
  action: "navigate" | "refresh" | "submit" | "page_up" | "page_down" | "exit" | "help" | "cancel" | "custom";
  target?: string;
  label?: string;
}

/** API mapping for a screen — how form submission maps to REST calls. */
export interface ApiMapping {
  submit: {
    method: "GET" | "POST" | "PUT" | "DELETE";
    path: string;
  };
  responseMap: Record<string, string>;
  /**
   * Optional static fallback response. Used when `apiBase` is empty
   * (static demo mode) or when a live fetch fails. Keys match the `path`
   * after interpolation (use "*" as a catch-all).
   */
  mockResponses?: Record<string, unknown>;
}

/** Menu-style navigation on a screen — resolves an input field value to a screen id. */
export interface MenuNavigation {
  field: string;
  targets: Record<string, string>;
}

/** A single screen definition — equivalent to a 3270 BMS map. */
export interface ScreenDefinition {
  screenId: string;
  title: string;
  fields: ScreenField[];
  apiMapping?: ApiMapping;
  menu?: MenuNavigation;
  functionKeys: Record<string, FunctionKeyMapping>;
  statusLine?: string;
}

/** Terminal theme. */
export type TerminalTheme = "green" | "amber" | "white";

/** Terminal dimensions. */
export interface TerminalDimensions {
  rows: number;
  cols: number;
}

/**
 * Sizing mode.
 * - "fixed": pixel-locked 80×24 (default — classic 3270 feel).
 * - "fluid": scales to fill container width; character grid stays 80×24.
 */
export type TerminalFit = "fixed" | "fluid";

/** Props for the terminal emulator component. */
export interface TerminalEmulatorProps {
  screens: Record<string, ScreenDefinition>;
  initialScreen: string;
  apiBase: string;
  theme?: TerminalTheme;
  dimensions?: TerminalDimensions;
  fit?: TerminalFit;
  onExit?: () => void;
  authToken?: string;
  /**
   * When set, the terminal persists state to `localStorage[\`terminal:${persistKey}\`]`.
   * Restores current screen + each screen's field values across page reloads.
   * When unset, state is ephemeral.
   */
  persistKey?: string;
  /** Called whenever the visible screen changes (mount + navigation). */
  onScreenChange?: (screenId: string) => void;
}

/** Persisted terminal state — current screen + per-screen field values. */
export interface TerminalPersistedState {
  currentScreenId: string;
  fieldValuesByScreen: Record<string, Record<string, string>>;
}

/** Internal state for a field value. */
export interface FieldState {
  name: string;
  value: string;
  row: number;
  col: number;
  length: number;
  type: "input" | "output" | "hidden";
}
