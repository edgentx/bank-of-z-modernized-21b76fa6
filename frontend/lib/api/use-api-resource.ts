'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { ApiError } from './client';

export type ApiResourceState<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: ApiError };

export interface UseApiResourceResult<T> {
  state: ApiResourceState<T>;
  refresh: () => void;
}

export function useApiResource<T>(
  fetcher: (signal: AbortSignal) => Promise<T>,
  deps: ReadonlyArray<unknown>,
): UseApiResourceResult<T> {
  const [state, setState] = useState<ApiResourceState<T>>({ status: 'idle' });
  const tick = useRef(0);

  const run = useCallback(
    () => {
      const myTick = ++tick.current;
      const controller = new AbortController();
      setState({ status: 'loading' });
      fetcher(controller.signal)
        .then((data) => {
          if (myTick !== tick.current) return;
          setState({ status: 'success', data });
        })
        .catch((err: unknown) => {
          if (myTick !== tick.current) return;
          if (controller.signal.aborted) return;
          const apiErr =
            err instanceof ApiError
              ? err
              : new ApiError(0, {
                  code: 'UNKNOWN',
                  message: err instanceof Error ? err.message : String(err),
                });
          setState({ status: 'error', error: apiErr });
        });
      return () => controller.abort();
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [...deps],
  );

  useEffect(() => {
    const cancel = run();
    return cancel;
  }, [run]);

  return {
    state,
    refresh: () => {
      run();
    },
  };
}
