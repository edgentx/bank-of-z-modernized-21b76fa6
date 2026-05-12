'use client';

import { FormEvent, useCallback, useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { DataTable, type DataTableColumn } from '@/components/ui/data-table';
import { DefaultErrorFallback, ErrorBoundary } from '@/components/ui/error-boundary';
import { Input } from '@/components/ui/input';
import { Spinner } from '@/components/ui/spinner';
import {
  accountsApi,
  type AccountDetail,
  type AccountStatus,
  type AccountSummary,
  type AccountTransaction,
} from '@/lib/api/accounts';
import type { Page } from '@/lib/api/types';
import { useApiResource } from '@/lib/api/use-api-resource';
import { useAuth } from '@/lib/auth/context';
import { formatDate, formatDateTime, formatMoney } from '@/lib/format';

const VIEW_ROLES = ['TELLER', 'SUPERVISOR', 'BRANCH_MANAGER'];

const STATUS_FILTERS: ReadonlyArray<{ label: string; value: AccountStatus | 'ALL' }> = [
  { label: 'All', value: 'ALL' },
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Dormant', value: 'DORMANT' },
  { label: 'Frozen', value: 'FROZEN' },
  { label: 'Closed', value: 'CLOSED' },
];

interface ListFilter {
  query: string;
  status: AccountStatus | 'ALL';
}

const EMPTY_FILTER: ListFilter = { query: '', status: 'ALL' };

export function AccountsView() {
  const auth = useAuth();
  const [draftQuery, setDraftQuery] = useState('');
  const [filter, setFilter] = useState<ListFilter>(EMPTY_FILTER);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const onSubmit = useCallback(
    (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      setFilter((prev) => ({ ...prev, query: draftQuery.trim() }));
    },
    [draftQuery],
  );

  const onStatusChange = useCallback((status: AccountStatus | 'ALL') => {
    setFilter((prev) => ({ ...prev, status }));
  }, []);

  if (!auth.authenticated) {
    return (
      <Card
        title="Sign-in required"
        description="Account records are only visible to authenticated teller staff."
      />
    );
  }
  if (!auth.hasAnyRole(VIEW_ROLES)) {
    return (
      <Card
        title="Insufficient role"
        description={`Account management requires one of: ${VIEW_ROLES.join(', ')}.`}
      />
    );
  }

  return (
    <section aria-labelledby="accounts-heading" className="space-y-6">
      <header>
        <h1 id="accounts-heading" className="text-3xl font-semibold text-teller">
          Account management
        </h1>
        <p className="mt-2 text-slate-600">
          Browse accounts, inspect balances and review the recent transaction ledger.
        </p>
      </header>

      <Card title="Filters" description="Narrow the list by account number, customer ID, or status.">
        <form onSubmit={onSubmit} className="mt-3 flex flex-wrap items-end gap-3" role="search">
          <Input
            name="account-query"
            label="Account or customer ID"
            placeholder="e.g. 20123456 or CUS-001"
            value={draftQuery}
            onChange={(event) => setDraftQuery(event.target.value)}
            className="w-72"
            autoComplete="off"
          />
          <fieldset className="flex flex-wrap items-center gap-2" aria-label="Status filter">
            <legend className="sr-only">Status</legend>
            {STATUS_FILTERS.map((option) => (
              <label key={option.value} className="flex items-center gap-2 text-sm text-slate-700">
                <input
                  type="radio"
                  name="account-status-filter"
                  value={option.value}
                  checked={filter.status === option.value}
                  onChange={() => onStatusChange(option.value)}
                />
                {option.label}
              </label>
            ))}
          </fieldset>
          <Button type="submit" variant="primary">
            Apply
          </Button>
        </form>
      </Card>

      <div className="grid gap-6 lg:grid-cols-[minmax(0,3fr)_minmax(0,2fr)]">
        <ErrorBoundary
          fallback={(error, reset) => (
            <DefaultErrorFallback error={error} reset={reset} title="Account list failed" />
          )}
        >
          <AccountList
            filter={filter}
            selectedId={selectedId}
            onSelect={(row) => setSelectedId(row.accountId)}
          />
        </ErrorBoundary>

        <ErrorBoundary
          fallback={(error, reset) => (
            <DefaultErrorFallback error={error} reset={reset} title="Account detail failed" />
          )}
        >
          <AccountDetailPane accountId={selectedId} />
        </ErrorBoundary>
      </div>
    </section>
  );
}

function AccountList({
  filter,
  selectedId,
  onSelect,
}: {
  filter: ListFilter;
  selectedId: string | null;
  onSelect: (row: AccountSummary) => void;
}) {
  const query = useMemo(() => {
    const looksNumeric = /^\d+$/.test(filter.query);
    return {
      accountNumber: looksNumeric ? filter.query : undefined,
      customerId: !looksNumeric && filter.query.length > 0 ? filter.query : undefined,
      status: filter.status === 'ALL' ? undefined : filter.status,
      size: 25,
    };
  }, [filter.query, filter.status]);

  const { state, refresh } = useApiResource<Page<AccountSummary>>(
    (signal) => accountsApi.list(query, signal),
    [query.accountNumber, query.customerId, query.status, query.size],
  );

  if (state.status === 'idle' || state.status === 'loading') {
    return (
      <div aria-busy="true" className="rounded-md border border-slate-200 bg-white p-6">
        <Spinner label="Loading accounts" />
      </div>
    );
  }
  if (state.status === 'error') {
    return (
      <div role="alert" className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900">
        <p className="font-semibold">Could not load accounts</p>
        <p className="mt-1">{state.error.message}</p>
        <Button variant="secondary" className="mt-3" onClick={refresh}>
          Retry
        </Button>
      </div>
    );
  }

  const columns: ReadonlyArray<DataTableColumn<AccountSummary>> = [
    {
      key: 'number',
      header: 'Account #',
      render: (row) => <code className="text-xs text-slate-800">{row.accountNumber}</code>,
    },
    { key: 'customer', header: 'Customer', render: (row) => row.customerName || row.customerId },
    { key: 'type', header: 'Type', render: (row) => row.accountType },
    {
      key: 'balance',
      header: 'Balance',
      align: 'right',
      render: (row) => formatMoney(row.balance, row.currency),
    },
    {
      key: 'status',
      header: 'Status',
      render: (row) => <AccountStatusBadge status={row.status} />,
    },
  ];

  return (
    <DataTable
      caption="Accounts matching the current filter"
      columns={columns}
      rows={state.data.items}
      rowKey={(row) => row.accountId}
      selectedRowKey={selectedId ?? undefined}
      onRowSelect={onSelect}
      emptyMessage="No accounts match the current filter."
    />
  );
}

function AccountDetailPane({ accountId }: { accountId: string | null }) {
  if (!accountId) {
    return (
      <Card
        title="Account detail"
        description="Select an account from the list to see balances, KYC and transaction history."
      />
    );
  }
  return <AccountDetailLoader accountId={accountId} />;
}

function AccountDetailLoader({ accountId }: { accountId: string }) {
  const { state, refresh } = useApiResource<AccountDetail>(
    (signal) => accountsApi.get(accountId, signal),
    [accountId],
  );

  if (state.status === 'idle' || state.status === 'loading') {
    return (
      <div aria-busy="true" className="rounded-md border border-slate-200 bg-white p-6">
        <Spinner label="Loading account" />
      </div>
    );
  }
  if (state.status === 'error') {
    return (
      <div role="alert" className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900">
        <p className="font-semibold">Could not load account</p>
        <p className="mt-1">{state.error.message}</p>
        <Button variant="secondary" className="mt-3" onClick={refresh}>
          Retry
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <AccountDetailCard account={state.data} />
      <ErrorBoundary
        fallback={(error, reset) => (
          <DefaultErrorFallback error={error} reset={reset} title="Transaction history failed" />
        )}
      >
        <TransactionHistory accountId={state.data.accountId} currency={state.data.currency} />
      </ErrorBoundary>
    </div>
  );
}

function AccountDetailCard({ account }: { account: AccountDetail }) {
  const rows: ReadonlyArray<{ label: string; value: string }> = [
    { label: 'Account #', value: account.accountNumber },
    { label: 'Customer', value: `${account.customerName} (${account.customerId})` },
    { label: 'Type', value: account.accountType },
    { label: 'Status', value: account.status },
    { label: 'Branch', value: account.branch || account.sortCode },
    { label: 'Sort code', value: account.sortCode },
    { label: 'Balance', value: formatMoney(account.balance, account.currency) },
    { label: 'Available', value: formatMoney(account.availableBalance, account.currency) },
    { label: 'Overdraft', value: formatMoney(account.overdraftLimit, account.currency) },
    { label: 'Opened', value: formatDate(account.openedAt) },
    { label: 'Updated', value: formatDateTime(account.updatedAt) },
  ];
  return (
    <Card title={account.accountNumber} description={`${account.accountType} · ${account.currency}`}>
      <dl className="grid grid-cols-1 gap-x-6 gap-y-2 text-sm sm:grid-cols-2">
        {rows.map((row) => (
          <div key={row.label} className="flex flex-col">
            <dt className="text-xs uppercase tracking-wide text-slate-500">{row.label}</dt>
            <dd className="text-slate-800">{row.value}</dd>
          </div>
        ))}
      </dl>
    </Card>
  );
}

function TransactionHistory({
  accountId,
  currency,
}: {
  accountId: string;
  currency: string;
}) {
  const { state, refresh } = useApiResource<Page<AccountTransaction>>(
    (signal) => accountsApi.transactions(accountId, { size: 25 }, signal),
    [accountId],
  );

  if (state.status === 'idle' || state.status === 'loading') {
    return (
      <div aria-busy="true" className="rounded-md border border-slate-200 bg-white p-6">
        <Spinner label="Loading transactions" />
      </div>
    );
  }
  if (state.status === 'error') {
    return (
      <div role="alert" className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900">
        <p className="font-semibold">Could not load transactions</p>
        <p className="mt-1">{state.error.message}</p>
        <Button variant="secondary" className="mt-3" onClick={refresh}>
          Retry
        </Button>
      </div>
    );
  }

  const columns: ReadonlyArray<DataTableColumn<AccountTransaction>> = [
    {
      key: 'when',
      header: 'When',
      render: (row) => formatDateTime(row.postedAt),
    },
    { key: 'type', header: 'Type', render: (row) => row.type },
    { key: 'description', header: 'Description', render: (row) => row.description },
    {
      key: 'amount',
      header: 'Amount',
      align: 'right',
      render: (row) => (
        <span className={signedAmountClass(row)}>{formatMoney(row.amount, row.currency)}</span>
      ),
    },
    {
      key: 'balance',
      header: 'Balance',
      align: 'right',
      render: (row) => formatMoney(row.runningBalance, row.currency || currency),
    },
  ];

  return (
    <section aria-labelledby="transactions-heading" className="space-y-2">
      <h2 id="transactions-heading" className="text-lg font-semibold text-teller">
        Transaction history
      </h2>
      <DataTable
        caption="Recent transactions for the selected account"
        columns={columns}
        rows={state.data.items}
        rowKey={(row) => row.transactionId}
        emptyMessage="No transactions posted yet."
      />
    </section>
  );
}

function signedAmountClass(row: AccountTransaction): string {
  const credit = row.type === 'DEPOSIT' || row.type === 'TRANSFER_IN' || row.type === 'INTEREST';
  return credit ? 'text-emerald-700' : 'text-red-700';
}

function AccountStatusBadge({ status }: { status: AccountStatus }) {
  const palette: Record<AccountStatus, string> = {
    ACTIVE: 'bg-emerald-100 text-emerald-800',
    DORMANT: 'bg-slate-200 text-slate-700',
    FROZEN: 'bg-amber-100 text-amber-800',
    CLOSED: 'bg-red-100 text-red-800',
  };
  return (
    <span
      className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ${palette[status]}`}
    >
      {status}
    </span>
  );
}
