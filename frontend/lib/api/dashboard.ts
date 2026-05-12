// Dashboard summary DTO for the teller landing page.
//
// Aggregates lightweight counters from the read-side projections so the
// dashboard can render without N round-trips. Handler stories will wire
// these counters; until then the page surfaces a graceful empty state.

import { api } from './client';

export interface DashboardSummary {
  generatedAt: string;
  tellerId: string;
  branch: string;
  openAccountCount: number;
  closedTodayCount: number;
  activeCustomerCount: number;
  pendingTransactionCount: number;
  postedTransactionCount: number;
  totalDepositsToday: number;
  totalWithdrawalsToday: number;
  currency: string;
}

export const dashboardApi = {
  summary: (signal?: AbortSignal): Promise<DashboardSummary> =>
    api.get<DashboardSummary>('/dashboard/summary', { signal }),
};
