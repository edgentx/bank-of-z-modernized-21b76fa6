export { api, apiClient, createApiClient, ApiError } from './client';
export type { ApiErrorBody, Page, PageRequest, RequestOptions } from './client';

export { accountsApi } from './accounts';
export type {
  AccountDetail,
  AccountSearchQuery,
  AccountStatus,
  AccountSummary,
  AccountTransaction,
} from './accounts';

export { customersApi } from './customers';
export type { CustomerDetail, CustomerSearchQuery, CustomerSummary } from './customers';

export { dashboardApi } from './dashboard';
export type { DashboardSummary } from './dashboard';

export { useApiResource } from './use-api-resource';
export type { ApiResourceState, UseApiResourceResult } from './use-api-resource';
