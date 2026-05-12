'use client';

import {
  ChangeEvent,
  KeyboardEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { cn } from '@/lib/utils';
import {
  firstFieldIndex,
  mapKey,
  nextFieldIndex,
  prevFieldIndex,
  shouldConsume,
} from '@/lib/terminal';
import type { ScreenField, ScreenMap } from '@/lib/api/terminal';

export interface TerminalEmulatorProps {
  screen: ScreenMap;
  /** Fired when the operator presses Enter (AID=ENTER). */
  onSubmit: (values: Record<string, string>) => void;
  /** Fired on F3 — host should pop back to the previous screen. */
  onExit?: () => void;
  /** Fired on Esc/Pause — clear the screen. */
  onClear?: () => void;
}

/**
 * Renders a 3270 screen map and preserves the BMS keyboard idioms tellers
 * rely on. Tab / Shift+Tab move between unprotected fields, Enter submits,
 * F3 exits, Esc clears. Cursor focus is mirrored visually so the operator
 * always knows where typed input will land.
 */
export function TerminalEmulator({ screen, onSubmit, onExit, onClear }: TerminalEmulatorProps) {
  const initialValues = useMemo(() => {
    const seed: Record<string, string> = {};
    for (const field of screen.fields) {
      if (!field.protected) seed[field.name] = field.value ?? '';
    }
    return seed;
  }, [screen.fields]);

  const [values, setValues] = useState<Record<string, string>>(initialValues);
  const [cursor, setCursor] = useState<number | null>(() => firstFieldIndex(screen.fields));
  const inputRefs = useRef<Array<HTMLInputElement | null>>([]);

  useEffect(() => {
    setValues(initialValues);
    setCursor(firstFieldIndex(screen.fields));
  }, [initialValues, screen.fields]);

  useEffect(() => {
    if (cursor === null) return;
    inputRefs.current[cursor]?.focus();
  }, [cursor]);

  const submit = useCallback(() => {
    onSubmit({ ...values });
  }, [onSubmit, values]);

  const handleKeyDown = useCallback(
    (event: KeyboardEvent<HTMLDivElement | HTMLInputElement>) => {
      const action = mapKey(event);
      if (!shouldConsume(action)) return;

      event.preventDefault();
      event.stopPropagation();

      switch (action) {
        case 'NEXT_FIELD':
          setCursor((cur) => nextFieldIndex(screen.fields, cur));
          break;
        case 'PREV_FIELD':
          setCursor((cur) => prevFieldIndex(screen.fields, cur));
          break;
        case 'SUBMIT':
          submit();
          break;
        case 'EXIT':
          onExit?.();
          break;
        case 'CLEAR':
          setValues(initialValues);
          setCursor(firstFieldIndex(screen.fields));
          onClear?.();
          break;
        default:
          break;
      }
    },
    [screen.fields, submit, onExit, onClear, initialValues],
  );

  const handleChange = useCallback(
    (field: ScreenField) => (event: ChangeEvent<HTMLInputElement>) => {
      const next = event.target.value.slice(0, field.length || event.target.value.length);
      setValues((prev) => ({ ...prev, [field.name]: next }));
    },
    [],
  );

  return (
    <div
      role="application"
      aria-label={`3270 terminal — ${screen.title}`}
      className="terminal-emulator"
      onKeyDown={handleKeyDown}
      data-screen-id={screen.screenId}
    >
      <div className="mb-2 flex items-baseline justify-between text-xs uppercase tracking-widest text-emerald-300">
        <span>{screen.title}</span>
        <span aria-hidden="true">{screen.rows}×{screen.cols}</span>
      </div>
      <div
        className={cn(
          'relative mx-auto aspect-[80/24] w-full max-w-5xl overflow-hidden',
          'rounded-md border-2 border-emerald-700 bg-black p-4 font-mono text-emerald-300',
          'shadow-[inset_0_0_60px_rgba(16,185,129,0.25)]',
        )}
        style={{
          containerType: 'inline-size',
        }}
      >
        <div
          className="grid h-full w-full"
          style={{
            gridTemplateColumns: `repeat(${screen.cols}, minmax(0, 1fr))`,
            gridTemplateRows: `repeat(${screen.rows}, minmax(0, 1fr))`,
            fontSize: 'clamp(8px, 1.2cqw, 18px)',
            lineHeight: 1.1,
          }}
        >
          {screen.fields.map((field, index) => (
            <TerminalField
              key={`${field.name}-${index}`}
              field={field}
              cols={screen.cols}
              focused={cursor === index}
              value={field.protected ? (field.label ?? field.value ?? '') : values[field.name] ?? ''}
              onFocus={() => setCursor(index)}
              onChange={handleChange(field)}
              registerRef={(el) => {
                inputRefs.current[index] = el;
              }}
            />
          ))}
        </div>
      </div>
      <p className="mt-3 text-xs text-slate-500">
        <span className="font-semibold text-slate-600">Keys:</span>
        {' '}Tab / Shift+Tab — fields · Enter — submit · F3 — exit · Esc — clear
      </p>
    </div>
  );
}

interface TerminalFieldProps {
  field: ScreenField;
  cols: number;
  focused: boolean;
  value: string;
  onFocus: () => void;
  onChange: (event: ChangeEvent<HTMLInputElement>) => void;
  registerRef: (el: HTMLInputElement | null) => void;
}

function TerminalField({
  field,
  cols,
  focused,
  value,
  onFocus,
  onChange,
  registerRef,
}: TerminalFieldProps) {
  const span = Math.max(1, Math.min(cols - (field.col - 1), field.length || (field.label?.length ?? value.length) || 1));
  const style = {
    gridRow: field.row,
    gridColumnStart: field.col,
    gridColumnEnd: `span ${span}`,
  } as const;

  const highlightClass = (() => {
    switch (field.highlight) {
      case 'BRIGHT':
        return 'text-emerald-100 font-semibold';
      case 'REVERSE':
        return 'bg-emerald-300 text-black';
      default:
        return 'text-emerald-300';
    }
  })();

  if (field.protected) {
    return (
      <div
        style={style}
        className={cn('whitespace-pre overflow-hidden', highlightClass)}
        data-field-name={field.name}
        data-protected="true"
      >
        {value}
      </div>
    );
  }

  return (
    <input
      ref={registerRef}
      type="text"
      name={field.name}
      data-field-name={field.name}
      data-cursor={focused ? 'on' : 'off'}
      aria-label={field.name}
      maxLength={field.length || undefined}
      value={value}
      onFocus={onFocus}
      onChange={onChange}
      style={style}
      className={cn(
        'border-b border-dashed border-emerald-700 bg-transparent px-0 py-0',
        'outline-none focus:border-emerald-300 focus:bg-emerald-950/40',
        focused && 'border-emerald-300 bg-emerald-950/40 ring-1 ring-emerald-400',
        highlightClass,
      )}
    />
  );
}
