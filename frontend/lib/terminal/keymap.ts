// 3270 keyboard mapping.
//
// The teller staff at Bank-of-Z built decades of muscle memory around BMS
// green-screens; the modern Next.js workstation has to preserve at least
// Tab / Shift+Tab / F3 / Enter on the same input model. Mapping is kept as
// a pure function over `KeyEventLike` so it can be unit-tested under
// node:test without rendering React.

/**
 * Subset of `KeyboardEvent` we depend on. Accepts the real DOM type as well
 * as React's SyntheticEvent shape and any plain-object fixture used in tests.
 */
export interface KeyEventLike {
  key: string;
  shiftKey?: boolean;
  ctrlKey?: boolean;
  altKey?: boolean;
  metaKey?: boolean;
}

export type TerminalAction = 'NEXT_FIELD' | 'PREV_FIELD' | 'SUBMIT' | 'EXIT' | 'CLEAR' | 'NONE';

/**
 * Translate a keyboard event into the 3270 action that the host expects.
 *
 * Conventions preserved:
 *   - Tab           → next unprotected field
 *   - Shift+Tab     → previous unprotected field
 *   - Enter / F5    → submit the screen (AID=ENTER / refresh)
 *   - F3            → exit / back to previous screen (AID=PF3)
 *   - Esc / Pause   → clear (AID=CLEAR)
 *
 * Modifier-augmented combinations (Ctrl+Enter, Cmd+Tab, etc.) are passed
 * through as `NONE` so browser shortcuts keep working.
 */
export function mapKey(event: KeyEventLike): TerminalAction {
  if (event.ctrlKey || event.metaKey || event.altKey) return 'NONE';

  if (event.key === 'Tab') {
    return event.shiftKey ? 'PREV_FIELD' : 'NEXT_FIELD';
  }
  if (event.key === 'Enter' || event.key === 'F5') return 'SUBMIT';
  if (event.key === 'F3') return 'EXIT';
  if (event.key === 'Escape' || event.key === 'Pause') return 'CLEAR';

  return 'NONE';
}

/**
 * True when the action requires consuming the underlying browser event
 * (preventDefault / stopPropagation) so the page does not also act on it —
 * e.g. Tab moves focus normally, Enter submits the surrounding form.
 */
export function shouldConsume(action: TerminalAction): boolean {
  return action !== 'NONE';
}
