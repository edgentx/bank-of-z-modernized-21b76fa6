"use client";

import { useState, useCallback, useRef, useEffect, useMemo } from "react";
import type {
  TerminalEmulatorProps,
  TerminalPersistedState,
  TerminalTheme,
} from "./terminal-types";

const STORAGE_PREFIX = "terminal:";

function loadPersistedState(
  persistKey: string | undefined,
  initialScreen: string,
): TerminalPersistedState {
  if (typeof window === "undefined" || !persistKey) {
    return { currentScreenId: initialScreen, fieldValuesByScreen: {} };
  }
  try {
    const raw = window.localStorage.getItem(STORAGE_PREFIX + persistKey);
    if (!raw) return { currentScreenId: initialScreen, fieldValuesByScreen: {} };
    const parsed = JSON.parse(raw) as Partial<TerminalPersistedState>;
    return {
      currentScreenId: parsed.currentScreenId ?? initialScreen,
      fieldValuesByScreen: parsed.fieldValuesByScreen ?? {},
    };
  } catch {
    return { currentScreenId: initialScreen, fieldValuesByScreen: {} };
  }
}

const THEME_COLORS: Record<TerminalTheme, { fg: string; bg: string; dim: string; bright: string; cursor: string }> = {
  green:  { fg: "#33ff33", bg: "#0a0f0a", dim: "#1a7a1a", bright: "#66ff66", cursor: "#33ff33" },
  amber:  { fg: "#ffb000", bg: "#1a1000", dim: "#805800", bright: "#ffd060", cursor: "#ffb000" },
  white:  { fg: "#c0c0c0", bg: "#0a0a0a", dim: "#606060", bright: "#ffffff", cursor: "#ffffff" },
};

/** Render one character cell. */
function Cell({ char, fg, bright, cursor }: { char: string; fg: string; bright?: boolean; cursor?: boolean }) {
  return (
    <span
      style={{
        color: fg,
        fontWeight: bright ? 700 : 400,
        textShadow: bright ? `0 0 6px ${fg}` : `0 0 2px ${fg}40`,
        background: cursor ? fg : "transparent",
        ...(cursor ? { color: "#000" } : {}),
      }}
    >
      {char}
    </span>
  );
}

export function TerminalEmulator({
  screens,
  initialScreen,
  apiBase,
  theme = "green",
  dimensions = { rows: 24, cols: 80 },
  fit = "fixed",
  onExit,
  authToken,
  persistKey,
  onScreenChange,
}: TerminalEmulatorProps) {
  const colors = THEME_COLORS[theme];
  const { rows, cols } = dimensions;

  const [persisted, setPersisted] = useState<TerminalPersistedState>(() =>
    loadPersistedState(persistKey, initialScreen),
  );
  const currentScreenId = persisted.currentScreenId;
  const fieldValues = useMemo(
    () => persisted.fieldValuesByScreen[currentScreenId] ?? {},
    [persisted, currentScreenId],
  );

  const setFieldValues = useCallback(
    (updater: (prev: Record<string, string>) => Record<string, string>) => {
      setPersisted((ps) => {
        const current = ps.fieldValuesByScreen[ps.currentScreenId] ?? {};
        const next = updater(current);
        if (next === current) return ps;
        return {
          ...ps,
          fieldValuesByScreen: { ...ps.fieldValuesByScreen, [ps.currentScreenId]: next },
        };
      });
    },
    [],
  );

  const [activeFieldIdx, setActiveFieldIdx] = useState(0);
  const [statusMessage, setStatusMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  // Hidden input overlaid on the terminal so tapping it summons the mobile
  // virtual keyboard (tabIndex divs don't). Desktop key handling is unchanged
  // because handleKeyDown preventDefaults all characters before they hit the
  // input value; onInput is only reached when a mobile IME skips keydown.
  const inputRef = useRef<HTMLInputElement>(null);

  const screen = screens[currentScreenId];
  const inputFields = useMemo(
    () => (screen?.fields ?? []).filter((f) => f.type === "input"),
    [screen],
  );

  // Reset ephemeral state when screen changes (field values persist per-screen).
  useEffect(() => {
    if (!screen) return;
    setActiveFieldIdx(0);
    setStatusMessage(screen.statusLine ?? "");
    onScreenChange?.(currentScreenId);
  }, [currentScreenId, screen, onScreenChange]);

  // Focus the hidden input on mount / screen change so physical keyboards
  // work immediately and Backspace is always targetable on mobile.
  useEffect(() => {
    inputRef.current?.focus();
  }, [currentScreenId]);

  // Persist state to localStorage whenever it changes.
  useEffect(() => {
    if (typeof window === "undefined" || !persistKey) return;
    try {
      window.localStorage.setItem(STORAGE_PREFIX + persistKey, JSON.stringify(persisted));
    } catch {
      // storage quota or private mode — silently ignore
    }
  }, [persisted, persistKey]);

  // --- Navigation (declared early so submitScreen can use it) ---
  const navigateTo = useCallback(
    (screenId: string) => {
      if (screens[screenId]) {
        setPersisted((ps) => ({ ...ps, currentScreenId: screenId }));
      }
    },
    [screens],
  );

  const applyResponse = useCallback(
    (data: unknown, responseMap: Record<string, string>) => {
      setFieldValues((prev) => {
        const next = { ...prev };
        for (const [screenField, apiField] of Object.entries(responseMap)) {
          const val = apiField.split(".").reduce<unknown>(
            (o, k) => (o && typeof o === "object" ? (o as Record<string, unknown>)[k] : undefined),
            data,
          );
          if (val !== undefined) {
            const fmt = screen?.fields.find((f) => f.name === screenField)?.format;
            next[screenField] = formatValue(val, fmt);
          }
        }
        return next;
      });
    },
    [screen],
  );

  // --- Submit / navigate on Enter ---
  const submitScreen = useCallback(async () => {
    if (!screen) return;

    // Menu screens navigate based on an input field value.
    if (screen.menu) {
      const raw = fieldValues[screen.menu.field] ?? "";
      const key = raw.trim();
      const target = screen.menu.targets[key];
      if (target) {
        navigateTo(target);
      } else if (key.length > 0) {
        setStatusMessage(`Invalid option: ${key}`);
      }
      return;
    }

    if (!screen.apiMapping) return;
    const { submit, responseMap, mockResponses } = screen.apiMapping;

    // Interpolate path params from field values
    let path = submit.path;
    for (const [name, val] of Object.entries(fieldValues)) {
      path = path.replace(`{${name}}`, encodeURIComponent(val.trim()));
    }

    // Static-demo mode: when apiBase is empty and mockResponses exists,
    // skip the network and resolve locally.
    if (!apiBase && mockResponses) {
      const mock = mockResponses[path] ?? mockResponses["*"];
      if (mock !== undefined) {
        applyResponse(mock, responseMap);
        setStatusMessage("Ready");
        return;
      }
      setStatusMessage("Not found");
      return;
    }

    setLoading(true);
    setStatusMessage("Processing...");
    try {
      const headers: Record<string, string> = { "Content-Type": "application/json" };
      if (authToken) headers["Authorization"] = `Bearer ${authToken}`;

      const opts: RequestInit = { method: submit.method, headers };
      if (submit.method !== "GET") {
        const body: Record<string, string> = {};
        for (const f of screen.fields.filter((f) => f.type === "input")) {
          body[f.name] = fieldValues[f.name]?.trim() ?? "";
        }
        opts.body = JSON.stringify(body);
      }

      const resp = await fetch(`${apiBase}${path}`, opts);
      if (!resp.ok) {
        if (mockResponses) {
          const mock = mockResponses[path] ?? mockResponses["*"];
          if (mock !== undefined) {
            applyResponse(mock, responseMap);
            setStatusMessage("Ready (demo)");
            return;
          }
        }
        setStatusMessage(`Error: ${resp.status} ${resp.statusText}`);
        return;
      }
      const data = await resp.json();
      applyResponse(data, responseMap);
      setStatusMessage("Ready");
    } catch (err) {
      if (mockResponses) {
        const mock = mockResponses[path] ?? mockResponses["*"];
        if (mock !== undefined) {
          applyResponse(mock, responseMap);
          setStatusMessage("Ready (demo)");
          return;
        }
      }
      setStatusMessage(`Error: ${err instanceof Error ? err.message : "request failed"}`);
    } finally {
      setLoading(false);
    }
  }, [screen, fieldValues, apiBase, authToken, navigateTo, applyResponse]);

  // --- Function key handler ---
  const handleFunctionKey = useCallback(
    (key: string) => {
      const mapping = screen?.functionKeys[key];
      if (!mapping) return;
      switch (mapping.action) {
        case "submit":
          submitScreen();
          break;
        case "navigate":
          if (mapping.target) navigateTo(mapping.target);
          break;
        case "refresh":
          submitScreen();
          break;
        case "exit":
          onExit?.();
          break;
        case "help":
          setStatusMessage("F1=Help F3=Exit F5=Refresh Enter=Submit Tab=Next Field");
          break;
        case "cancel":
          setFieldValues(() => ({}));
          setStatusMessage("Cleared");
          break;
        default:
          break;
      }
    },
    [screen, submitScreen, navigateTo, onExit],
  );

  // --- Keyboard handler ---
  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      // Function keys
      if (e.key.startsWith("F") && e.key.length <= 3) {
        e.preventDefault();
        handleFunctionKey(e.key);
        return;
      }

      // Tab — next input field
      if (e.key === "Tab") {
        e.preventDefault();
        setActiveFieldIdx((prev) => (prev + 1) % Math.max(inputFields.length, 1));
        return;
      }

      // Shift+Tab — prev input field
      if (e.key === "Tab" && e.shiftKey) {
        e.preventDefault();
        setActiveFieldIdx((prev) => (prev - 1 + inputFields.length) % Math.max(inputFields.length, 1));
        return;
      }

      // Enter — submit
      if (e.key === "Enter") {
        e.preventDefault();
        submitScreen();
        return;
      }

      // Escape — exit
      if (e.key === "Escape") {
        e.preventDefault();
        onExit?.();
        return;
      }

      // Character input to active field
      const activeField = inputFields[activeFieldIdx];
      if (!activeField) return;

      if (e.key === "Backspace") {
        e.preventDefault();
        setFieldValues((prev) => ({
          ...prev,
          [activeField.name]: prev[activeField.name]?.slice(0, -1) ?? "",
        }));
        return;
      }

      if (e.key.length === 1 && !e.ctrlKey && !e.metaKey) {
        e.preventDefault();
        setFieldValues((prev) => {
          const current = prev[activeField.name] ?? "";
          if (current.length >= activeField.length) return prev;
          return { ...prev, [activeField.name]: current + e.key };
        });
      }
    },
    [inputFields, activeFieldIdx, handleFunctionKey, submitScreen, onExit],
  );

  // Mobile IMEs (Gboard, iOS) sometimes skip keydown for composed/autocorrected
  // text and fire only input events. Funnel those chars into the active field
  // the same way, then clear the input so nothing accumulates there.
  const handleInput = useCallback(
    (e: React.FormEvent<HTMLInputElement>) => {
      const val = e.currentTarget.value;
      e.currentTarget.value = "";
      if (!val) return;
      const activeField = inputFields[activeFieldIdx];
      if (!activeField) return;
      setFieldValues((prev) => {
        const current = prev[activeField.name] ?? "";
        const room = activeField.length - current.length;
        if (room <= 0) return prev;
        return { ...prev, [activeField.name]: current + val.slice(0, room) };
      });
    },
    [inputFields, activeFieldIdx],
  );

  // --- Render the 80x24 grid ---
  const renderGrid = useCallback(() => {
    if (!screen) return <div style={{ color: colors.fg }}>Screen not found: {currentScreenId}</div>;

    // Build a 2D character buffer
    const buffer: Array<{ char: string; fg: string; bright: boolean; cursor: boolean }[]> = [];
    for (let r = 0; r < rows; r++) {
      buffer[r] = [];
      for (let c = 0; c < cols; c++) {
        buffer[r][c] = { char: " ", fg: colors.fg, bright: false, cursor: false };
      }
    }

    // Title bar (row 0)
    const titleStr = ` ${screen.title} `;
    const titleStart = Math.floor((cols - titleStr.length) / 2);
    for (let i = 0; i < cols; i++) {
      const ch = i >= titleStart && i < titleStart + titleStr.length ? titleStr[i - titleStart] : "-";
      buffer[0][i] = { char: ch, fg: colors.bright, bright: true, cursor: false };
    }

    // Render fields
    for (const field of screen.fields) {
      const r = field.row;
      if (r < 0 || r >= rows) continue;

      // Label
      if (field.label) {
        for (let i = 0; i < field.label.length && field.col + i < cols; i++) {
          const c = field.col + i;
          if (c >= 0 && c < cols) {
            buffer[r][c] = { char: field.label[i], fg: colors.fg, bright: false, cursor: false };
          }
        }
      }

      // Value area
      const valueStart = field.col + (field.label?.length ?? 0);
      const value = fieldValues[field.name] ?? "";
      const isActive = inputFields[activeFieldIdx]?.name === field.name;

      for (let i = 0; i < field.length; i++) {
        const c = valueStart + i;
        if (c < 0 || c >= cols) continue;
        const ch = i < value.length ? value[i] : field.type === "input" ? "_" : " ";
        const isCursor = isActive && i === value.length;
        buffer[r][c] = {
          char: ch,
          fg: field.type === "input" ? colors.bright : colors.fg,
          bright: field.bright ?? field.type === "input",
          cursor: isCursor,
        };
      }
    }

    // Status line (row 22)
    const statusStr = statusMessage || "Ready";
    for (let i = 0; i < statusStr.length && i < cols; i++) {
      buffer[rows - 2][i] = { char: statusStr[i], fg: colors.dim, bright: false, cursor: false };
    }

    // Function key bar (row 23)
    const fkeyBar = Object.entries(screen.functionKeys)
      .map(([key, m]) => `${key}=${m.label ?? m.action}`)
      .join("  ");
    for (let i = 0; i < fkeyBar.length && i < cols; i++) {
      buffer[rows - 1][i] = { char: fkeyBar[i], fg: colors.dim, bright: false, cursor: false };
    }

    return buffer.map((row, ri) => (
      <div key={ri} style={{ height: "1.4em", whiteSpace: "pre" }}>
        {row.map((cell, ci) => (
          <Cell key={ci} char={cell.char} fg={cell.fg} bright={cell.bright} cursor={cell.cursor} />
        ))}
      </div>
    ));
  }, [screen, fieldValues, activeFieldIdx, inputFields, colors, rows, cols, statusMessage, currentScreenId]);

  // Fluid mode nests: the outer <div> establishes the query container, and
  // the inner <div> reads 100cqw to size its font. CSS container queries do
  // NOT apply to the container element's own styles — only to descendants —
  // so the two must be on separate elements.
  const isFluid = fit === "fluid";
  const outerStyle: React.CSSProperties = isFluid
    ? {
        width: "100%",
        containerType: "inline-size",
        outline: "none",
        position: "relative",
      }
    : {
        width: `${cols * 8.4 + 32}px`,
        outline: "none",
        position: "relative",
      };

  // Fluid font-size must fill the container width. Monospace char width is
  // ~0.6em, so 80 chars = 48em. To fit `cols * 0.6 em + padding` into 100cqw,
  // solve fontSize = (100cqw - padding_total) / (cols * 0.6). Padding is
  // clamp(8px,1.5cqw,20px) per side = clamp(16px,3cqw,40px) total.
  const innerStyle: React.CSSProperties = isFluid
    ? {
        background: colors.bg,
        padding: "clamp(8px, 1.5cqw, 20px)",
        borderRadius: "8px",
        border: `1px solid ${colors.fg}30`,
        fontFamily: "'JetBrains Mono', 'Fira Code', 'Courier New', monospace",
        fontSize: `calc((100cqw - clamp(16px, 3cqw, 40px)) / ${cols * 0.6})`,
        lineHeight: "1.4",
        cursor: "text",
        boxShadow: `0 0 20px ${colors.fg}15, inset 0 0 60px ${colors.bg}`,
        position: "relative",
        overflow: "hidden",
      }
    : {
        background: colors.bg,
        padding: "16px",
        borderRadius: "8px",
        border: `1px solid ${colors.fg}30`,
        fontFamily: "'JetBrains Mono', 'Fira Code', 'Courier New', monospace",
        fontSize: "14px",
        lineHeight: "1.4",
        cursor: "text",
        boxShadow: `0 0 20px ${colors.fg}15, inset 0 0 60px ${colors.bg}`,
        position: "relative",
        overflow: "hidden",
      };

  return (
    <div ref={containerRef} style={outerStyle}>
      {/*
        Hidden input overlaid on the terminal — summons the mobile virtual
        keyboard on tap (a plain focusable div doesn't). Visually invisible
        (opacity:0, transparent caret) but captures all keyboard/IME events.
        fontSize:16px prevents iOS Safari's zoom-on-focus.
      */}
      <input
        ref={inputRef}
        type="text"
        inputMode="text"
        autoCapitalize="off"
        autoCorrect="off"
        autoComplete="off"
        spellCheck={false}
        defaultValue=""
        onKeyDown={handleKeyDown}
        onInput={handleInput}
        aria-label="terminal input"
        style={{
          position: "absolute",
          inset: 0,
          width: "100%",
          height: "100%",
          opacity: 0,
          background: "transparent",
          border: 0,
          outline: 0,
          color: "transparent",
          caretColor: "transparent",
          fontSize: "16px",
          padding: 0,
          margin: 0,
          zIndex: 10,
        }}
      />
      <div style={innerStyle}>
        {/* CRT scanline effect */}
        <div
          style={{
            position: "absolute",
            inset: 0,
            background: `repeating-linear-gradient(0deg, transparent, transparent 1px, ${colors.bg}40 1px, ${colors.bg}40 2px)`,
            pointerEvents: "none",
            zIndex: 1,
          }}
        />
        <div style={{ position: "relative", zIndex: 2 }}>
          {loading && (
            <div style={{ position: "absolute", top: 4, right: 12, color: colors.bright, fontSize: "12px" }}>
              [PROCESSING]
            </div>
          )}
          {renderGrid()}
        </div>
      </div>
    </div>
  );
}

function formatValue(val: unknown, format?: string): string {
  if (val === null || val === undefined) return "";
  switch (format) {
    case "currency":
      return typeof val === "number" ? `$${val.toFixed(2)}` : String(val);
    case "number":
      return typeof val === "number" ? val.toLocaleString() : String(val);
    case "date":
      return typeof val === "string" ? val.substring(0, 10) : String(val);
    default:
      return String(val);
  }
}
