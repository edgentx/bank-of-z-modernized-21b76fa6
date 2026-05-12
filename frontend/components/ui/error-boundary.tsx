'use client';

import { Component, ErrorInfo, ReactNode } from 'react';
import { Button } from './button';

export interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: (error: Error, reset: () => void) => ReactNode;
  onError?: (error: Error, info: ErrorInfo) => void;
}

interface State {
  error: Error | null;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error): State {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    this.props.onError?.(error, info);
  }

  reset = (): void => {
    this.setState({ error: null });
  };

  render(): ReactNode {
    const { error } = this.state;
    if (error) {
      if (this.props.fallback) return this.props.fallback(error, this.reset);
      return <DefaultErrorFallback error={error} reset={this.reset} />;
    }
    return this.props.children;
  }
}

export interface ErrorFallbackProps {
  error: Error;
  reset: () => void;
  title?: string;
}

export function DefaultErrorFallback({
  error,
  reset,
  title = 'Something went wrong',
}: ErrorFallbackProps) {
  return (
    <div
      role="alert"
      className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900"
    >
      <h3 className="text-base font-semibold">{title}</h3>
      <p className="mt-1 text-red-800">{error.message}</p>
      <div className="mt-3">
        <Button variant="secondary" onClick={reset}>
          Try again
        </Button>
      </div>
    </div>
  );
}
