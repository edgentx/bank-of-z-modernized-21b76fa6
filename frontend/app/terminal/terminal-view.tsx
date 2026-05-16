'use client';

import { useCallback, useMemo, useState } from 'react';
import { Card } from '@/components/ui/card';
import { DefaultErrorFallback, ErrorBoundary } from '@/components/ui/error-boundary';
import { Spinner } from '@/components/ui/spinner';
import { Button } from '@/components/ui/button';
import { TerminalEmulator } from '@/components/terminal/terminal-emulator';
import type { ScreenMap } from '@/lib/api/terminal';
import { terminalApi } from '@/lib/api/terminal';
import { useApiResource } from '@/lib/api/use-api-resource';
import { useAuth } from '@/lib/auth/context';

const VIEW_ROLES = ['TELLER', 'SUPERVISOR', 'BRANCH_MANAGER'];

const DEFAULT_SCREEN_ID = 'MAINMENU';

/**
 * /terminal entry point. Resolves the requested screen-map from the backend
 * (defaulting to MAINMENU when the operator just opened the workstation)
 * and hands it to the 3270 emulator component.
 */
export function TerminalView() {
  const auth = useAuth();
  const [screenId, setScreenId] = useState<string>(DEFAULT_SCREEN_ID);
  const [history, setHistory] = useState<string[]>([]);
  const [hostScreen, setHostScreen] = useState<ScreenMap | null>(null);
  const [submissionError, setSubmissionError] = useState<string | null>(null);

  const { state, refresh } = useApiResource<ScreenMap>(
    (signal) => terminalApi.getScreen(screenId, signal),
    [screenId],
  );

  const back = useCallback(() => {
    setHistory((stack) => {
      if (stack.length === 0) return stack;
      const copy = stack.slice();
      const prev = copy.pop();
      if (prev) {
        setHostScreen(null);
        setScreenId(prev);
      }
      return copy;
    });
  }, []);

  const onSubmit = useCallback(
    async (values: Record<string, string>) => {
      setSubmissionError(null);
      try {
        const next = await terminalApi.submit({ screenId, values });
        setHostScreen(next);
        if (next.screenId !== screenId) {
          setHistory((stack) => [...stack, screenId]);
        }
        setScreenId(next.screenId);
      } catch (err) {
        setSubmissionError(err instanceof Error ? err.message : String(err));
      }
    },
    [screenId],
  );

  const canExit = history.length > 0;
  const displayScreen =
    hostScreen?.screenId === screenId ? hostScreen : state.status === 'success' ? state.data : null;

  const guard = useMemo(() => {
    if (!auth.authenticated) {
      return (
        <Card
          title="Sign-in required"
          description="The 3270 workstation is only available to authenticated teller staff."
        />
      );
    }
    if (!auth.hasAnyRole(VIEW_ROLES)) {
      return (
        <Card
          title="Insufficient role"
          description={`Terminal access requires one of: ${VIEW_ROLES.join(', ')}.`}
        />
      );
    }
    return null;
  }, [auth]);

  if (guard) return guard;

  return (
    <section aria-labelledby="terminal-heading" className="space-y-4">
      <header className="flex items-end justify-between gap-3">
        <div>
          <h1 id="terminal-heading" className="text-3xl font-semibold text-teller">
            3270 Terminal
          </h1>
          <p className="mt-1 text-slate-600">
            Browser-resident replacement for the CICS green-screen workstation. Tab / F3 / Enter
            preserve their host conventions so teller muscle memory carries over.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs text-slate-500">
          <span>Screen:</span>
          <code className="rounded bg-slate-100 px-2 py-1 text-slate-700">{screenId}</code>
          {canExit && (
            <Button variant="ghost" onClick={back} aria-label="Back to previous screen">
              ← Back
            </Button>
          )}
        </div>
      </header>

      <ErrorBoundary
        fallback={(error, reset) => (
          <DefaultErrorFallback error={error} reset={reset} title="Terminal screen failed" />
        )}
      >
        {!displayScreen && (state.status === 'idle' || state.status === 'loading') ? (
          <div aria-busy="true" className="rounded-md border border-slate-200 bg-white p-6">
            <Spinner label={`Loading ${screenId}`} />
          </div>
        ) : !displayScreen && state.status === 'error' ? (
          <div
            role="alert"
            className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900"
          >
            <p className="font-semibold">Could not load terminal screen</p>
            <p className="mt-1">{state.error.message}</p>
            <Button variant="secondary" className="mt-3" onClick={refresh}>
              Retry
            </Button>
          </div>
        ) : displayScreen ? (
          <TerminalEmulator
            screen={displayScreen}
            onSubmit={onSubmit}
            onExit={canExit ? back : undefined}
            onClear={() => setSubmissionError(null)}
          />
        ) : null}
      </ErrorBoundary>

      {submissionError && (
        <div
          role="alert"
          className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900"
        >
          <p className="font-semibold">Terminal command failed</p>
          <p className="mt-1">{submissionError}</p>
        </div>
      )}
    </section>
  );
}
