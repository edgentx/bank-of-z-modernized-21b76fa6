'use client';

import { FormEvent, useCallback, useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { DataTable, type DataTableColumn } from '@/components/ui/data-table';
import { DefaultErrorFallback, ErrorBoundary } from '@/components/ui/error-boundary';
import { Input } from '@/components/ui/input';
import { Spinner } from '@/components/ui/spinner';
import { customersApi, type CustomerSummary } from '@/lib/api/customers';
import type { Page } from '@/lib/api/types';
import { useApiResource } from '@/lib/api/use-api-resource';
import { useAuth } from '@/lib/auth/context';
import { formatDate } from '@/lib/format';

const ALLOWED_ROLES = ['TELLER', 'SUPERVISOR', 'BRANCH_MANAGER'];

type SearchMode = 'name' | 'accountNumber';

interface SearchTerm {
  mode: SearchMode;
  value: string;
}

const EMPTY_SEARCH: SearchTerm = { mode: 'name', value: '' };

export function CustomersView() {
  const auth = useAuth();
  const [draftMode, setDraftMode] = useState<SearchMode>('name');
  const [draftValue, setDraftValue] = useState('');
  const [active, setActive] = useState<SearchTerm>(EMPTY_SEARCH);

  const onSubmit = useCallback(
    (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      const value = draftValue.trim();
      if (value.length === 0) {
        setActive(EMPTY_SEARCH);
        return;
      }
      setActive({ mode: draftMode, value });
    },
    [draftMode, draftValue],
  );

  const onClear = useCallback(() => {
    setDraftValue('');
    setActive(EMPTY_SEARCH);
  }, []);

  if (!auth.authenticated) {
    return <RequireSignIn />;
  }
  if (!auth.hasAnyRole(ALLOWED_ROLES)) {
    return <Forbidden roles={ALLOWED_ROLES} />;
  }

  return (
    <section aria-labelledby="customers-heading" className="space-y-6">
      <header>
        <h1 id="customers-heading" className="text-3xl font-semibold text-teller">
          Customer lookup
        </h1>
        <p className="mt-2 text-slate-600">
          Search by name or by account number to surface customer records and recent KYC details.
        </p>
      </header>

      <Card title="Search" description="Choose a search mode, type at least two characters, then press Search.">
        <form onSubmit={onSubmit} className="mt-3 flex flex-wrap items-end gap-3" role="search">
          <fieldset className="flex items-center gap-3" aria-label="Search mode">
            <legend className="sr-only">Search mode</legend>
            <label className="flex items-center gap-2 text-sm text-slate-700">
              <input
                type="radio"
                name="customer-search-mode"
                value="name"
                checked={draftMode === 'name'}
                onChange={() => setDraftMode('name')}
              />
              By name
            </label>
            <label className="flex items-center gap-2 text-sm text-slate-700">
              <input
                type="radio"
                name="customer-search-mode"
                value="accountNumber"
                checked={draftMode === 'accountNumber'}
                onChange={() => setDraftMode('accountNumber')}
              />
              By account number
            </label>
          </fieldset>
          <Input
            name="customer-search-value"
            label={draftMode === 'name' ? 'Customer name' : 'Account number'}
            placeholder={draftMode === 'name' ? 'e.g. Pat Morgan' : 'e.g. 20123456'}
            value={draftValue}
            onChange={(event) => setDraftValue(event.target.value)}
            className="w-72"
            autoComplete="off"
            inputMode={draftMode === 'accountNumber' ? 'numeric' : 'text'}
          />
          <Button type="submit" variant="primary">
            Search
          </Button>
          <Button type="button" variant="ghost" onClick={onClear}>
            Clear
          </Button>
        </form>
      </Card>

      <ErrorBoundary
        fallback={(error, reset) => (
          <DefaultErrorFallback error={error} reset={reset} title="Customer search failed" />
        )}
      >
        <CustomerResults active={active} />
      </ErrorBoundary>
    </section>
  );
}

function CustomerResults({ active }: { active: SearchTerm }) {
  const params = useMemo(
    () => ({
      name: active.mode === 'name' && active.value.length > 0 ? active.value : undefined,
      accountNumber:
        active.mode === 'accountNumber' && active.value.length > 0 ? active.value : undefined,
      size: 25,
    }),
    [active.mode, active.value],
  );

  const { state, refresh } = useApiResource<Page<CustomerSummary>>(
    (signal) => customersApi.search(params, signal),
    [params.name, params.accountNumber, params.size],
  );

  if (active.value.length === 0) {
    return (
      <div className="rounded-md border border-dashed border-slate-300 bg-white p-6 text-sm text-slate-600">
        Enter a search term above to look up customers.
      </div>
    );
  }

  if (state.status === 'idle' || state.status === 'loading') {
    return (
      <div aria-busy="true" className="rounded-md border border-slate-200 bg-white p-6">
        <Spinner label="Searching customers" />
      </div>
    );
  }

  if (state.status === 'error') {
    return (
      <div role="alert" className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-900">
        <p className="font-semibold">Could not search customers</p>
        <p className="mt-1">{state.error.message}</p>
        <Button variant="secondary" className="mt-3" onClick={refresh}>
          Retry
        </Button>
      </div>
    );
  }

  const columns: ReadonlyArray<DataTableColumn<CustomerSummary>> = [
    { key: 'name', header: 'Name', render: (row) => row.fullName },
    {
      key: 'id',
      header: 'Customer ID',
      render: (row) => <code className="text-xs text-slate-700">{row.customerId}</code>,
    },
    { key: 'email', header: 'Email', render: (row) => row.email || '—' },
    { key: 'phone', header: 'Phone', render: (row) => row.phone || '—' },
    {
      key: 'status',
      header: 'Status',
      render: (row) => <StatusBadge status={row.status} />,
    },
    {
      key: 'enrolled',
      header: 'Enrolled',
      render: (row) => formatDate(row.enrolledAt),
    },
  ];

  return (
    <DataTable
      caption={`Customers matching "${active.value}"`}
      columns={columns}
      rows={state.data.items}
      rowKey={(row) => row.customerId}
      emptyMessage={`No customers matched "${active.value}".`}
    />
  );
}

function StatusBadge({ status }: { status: CustomerSummary['status'] }) {
  const palette: Record<CustomerSummary['status'], string> = {
    ENROLLED: 'bg-emerald-100 text-emerald-800',
    SUSPENDED: 'bg-amber-100 text-amber-800',
    DELETED: 'bg-slate-200 text-slate-700',
  };
  return (
    <span
      className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ${palette[status]}`}
    >
      {status}
    </span>
  );
}

function RequireSignIn() {
  return (
    <Card
      title="Sign-in required"
      description="Customer records are only available to authenticated teller staff."
    />
  );
}

function Forbidden({ roles }: { roles: ReadonlyArray<string> }) {
  return (
    <Card
      title="Insufficient role"
      description={`Customer lookup requires one of: ${roles.join(', ')}.`}
    />
  );
}
