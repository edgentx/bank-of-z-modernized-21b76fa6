'use client';

import Link from 'next/link';
import { useCallback } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { DefaultErrorFallback, ErrorBoundary } from '@/components/ui/error-boundary';
import { Spinner } from '@/components/ui/spinner';
import { dashboardApi, type DashboardSummary } from '@/lib/api/dashboard';
import { useApiResource } from '@/lib/api/use-api-resource';
import { useAuth } from '@/lib/auth/context';
import { formatCount, formatDateTime, formatMoney } from '@/lib/format';

const TELLER_ROLES = ['TELLER', 'SUPERVISOR', 'BRANCH_MANAGER'];

type QuickAction = {
  href: '/accounts' | '/customers' | '/transactions';
  label: string;
  description: string;
  roles: ReadonlyArray<string>;
};

const QUICK_ACTIONS: ReadonlyArray<QuickAction> = [
  {
    href: '/accounts',
    label: 'Account management',
    description: 'Look up accounts, inspect balances and post status changes.',
    roles: TELLER_ROLES,
  },
  {
    href: '/customers',
    label: 'Customer lookup',
    description: 'Find customers by name or account number and review KYC.',
    roles: TELLER_ROLES,
  },
  {
    href: '/transactions',
    label: 'Post transaction',
    description: 'Deposit, withdraw or transfer between accounts.',
    roles: ['TELLER', 'SUPERVISOR'],
  },
];

export function DashboardView() {
  const auth = useAuth();
  const { state, refresh } = useApiResource<DashboardSummary>(
    (signal) => dashboardApi.summary(signal),
    [auth.user.userId],
  );

  const reset = useCallback(() => refresh(), [refresh]);

  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-3xl font-semibold text-teller">Teller dashboard</h1>
        <p className="mt-2 text-slate-600">
          Welcome back,{' '}
          <span className="font-medium text-teller">
            {auth.authenticated ? auth.user.userId : 'guest'}
          </span>
          . Live counters refresh from the Spring Boot teller-core service.
        </p>
      </header>

      <ErrorBoundary
        fallback={(error) => (
          <DefaultErrorFallback error={error} reset={reset} title="Dashboard unavailable" />
        )}
      >
        <SummarySection state={state} onRetry={reset} />
      </ErrorBoundary>

      <section aria-labelledby="quick-actions-heading">
        <h2 id="quick-actions-heading" className="text-xl font-semibold text-teller">
          Quick actions
        </h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {QUICK_ACTIONS.map((action) => {
            const visible = auth.authenticated && auth.hasAnyRole(action.roles);
            if (!visible) return null;
            return (
              <Link
                key={action.href}
                href={action.href}
                className="block rounded-lg focus:outline-none focus-visible:ring-2 focus-visible:ring-teller-accent"
              >
                <Card title={action.label} description={action.description} />
              </Link>
            );
          })}
          {!auth.authenticated && (
            <Card title="Sign in required" description="Quick actions appear after authentication.">
              <Link href="/login" className="inline-flex">
                <Button variant="primary">Continue to sign-in</Button>
              </Link>
            </Card>
          )}
        </div>
      </section>
    </div>
  );
}

function SummarySection({
  state,
  onRetry,
}: {
  state: ReturnType<typeof useApiResource<DashboardSummary>>['state'];
  onRetry: () => void;
}) {
  if (state.status === 'idle' || state.status === 'loading') {
    return (
      <section aria-busy="true" className="rounded-lg border border-slate-200 bg-white p-5">
        <Spinner label="Loading summary" />
      </section>
    );
  }

  if (state.status === 'error') {
    return (
      <section
        role="alert"
        className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900"
      >
        <h3 className="text-base font-semibold">Could not load dashboard summary</h3>
        <p className="mt-1 text-red-800">{state.error.message}</p>
        <div className="mt-3">
          <Button variant="secondary" onClick={onRetry}>
            Retry
          </Button>
        </div>
      </section>
    );
  }

  const { data } = state;
  const tiles: ReadonlyArray<{ key: string; label: string; value: string; helper?: string }> = [
    {
      key: 'open-accounts',
      label: 'Open accounts',
      value: formatCount(data.openAccountCount),
      helper: `${formatCount(data.closedTodayCount)} closed today`,
    },
    {
      key: 'active-customers',
      label: 'Active customers',
      value: formatCount(data.activeCustomerCount),
    },
    {
      key: 'pending-tx',
      label: 'Pending transactions',
      value: formatCount(data.pendingTransactionCount),
      helper: `${formatCount(data.postedTransactionCount)} posted today`,
    },
    {
      key: 'deposits',
      label: 'Deposits today',
      value: formatMoney(data.totalDepositsToday, data.currency),
    },
    {
      key: 'withdrawals',
      label: 'Withdrawals today',
      value: formatMoney(data.totalWithdrawalsToday, data.currency),
    },
  ];

  return (
    <section aria-labelledby="summary-heading">
      <div className="flex items-baseline justify-between">
        <h2 id="summary-heading" className="text-xl font-semibold text-teller">
          Today at branch {data.branch}
        </h2>
        <p className="text-xs text-slate-500">Updated {formatDateTime(data.generatedAt)}</p>
      </div>
      <dl className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {tiles.map((tile) => (
          <div
            key={tile.key}
            className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
          >
            <dt className="text-sm font-medium text-slate-500">{tile.label}</dt>
            <dd className="mt-1 text-2xl font-semibold text-teller" aria-live="polite">
              {tile.value}
            </dd>
            {tile.helper && <p className="mt-1 text-xs text-slate-500">{tile.helper}</p>}
          </div>
        ))}
      </dl>
    </section>
  );
}
