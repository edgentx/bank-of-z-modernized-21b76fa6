// Account resource DTOs + client surface for the Bank-of-Z teller frontend.
//
// Shapes mirror the AccountAggregate / AccountOpenedEvent fields exposed by
// the Spring Boot teller-core service. Field names are camelCase to match
// the REST envelope after Spring's Jackson default — handler stories will
// adjust the path roots if the backend gateway prefixes them.

import { api } from './client';
import type { Page, PageRequest } from './types';

export type AccountStatus = 'ACTIVE' | 'DORMANT' | 'CLOSED' | 'FROZEN';

export interface AccountSummary {
  accountId: string;
  accountNumber: string;
  customerId: string;
  customerName: string;
  accountType: string;
  status: AccountStatus;
  balance: number;
  currency: string;
  sortCode: string;
  openedAt: string;
}

export interface AccountDetail extends AccountSummary {
  availableBalance: number;
  overdraftLimit: number;
  branch: string;
  updatedAt: string;
}

export interface AccountTransaction {
  transactionId: string;
  accountId: string;
  postedAt: string;
  description: string;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER_IN' | 'TRANSFER_OUT' | 'FEE' | 'INTEREST';
  amount: number;
  currency: string;
  runningBalance: number;
}

export interface AccountSearchQuery extends PageRequest {
  customerId?: string;
  accountNumber?: string;
  status?: AccountStatus;
}

function toParams(query: AccountSearchQuery): Record<string, string | number> {
  const params: Record<string, string | number> = {};
  if (query.page !== undefined) params.page = query.page;
  if (query.size !== undefined) params.size = query.size;
  if (query.sort) params.sort = query.sort;
  if (query.customerId) params.customerId = query.customerId;
  if (query.accountNumber) params.accountNumber = query.accountNumber;
  if (query.status) params.status = query.status;
  return params;
}

export const accountsApi = {
  list: (query: AccountSearchQuery = {}, signal?: AbortSignal): Promise<Page<AccountSummary>> =>
    api.get<Page<AccountSummary>>('/accounts', { params: toParams(query), signal }),
  get: (accountId: string, signal?: AbortSignal): Promise<AccountDetail> =>
    api.get<AccountDetail>(`/accounts/${encodeURIComponent(accountId)}`, { signal }),
  transactions: (
    accountId: string,
    query: PageRequest = {},
    signal?: AbortSignal,
  ): Promise<Page<AccountTransaction>> =>
    api.get<Page<AccountTransaction>>(`/accounts/${encodeURIComponent(accountId)}/transactions`, {
      params: { page: query.page ?? 0, size: query.size ?? 20, sort: query.sort ?? 'postedAt,desc' },
      signal,
    }),
};
