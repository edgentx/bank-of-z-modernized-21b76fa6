// Customer resource DTOs + client surface for the Bank-of-Z teller frontend.
//
// Mirrors the CustomerAggregate / CustomerEnrolledEvent fields exposed by
// the Spring Boot teller-core service.

import { api } from './client';
import type { Page, PageRequest } from './types';

export interface CustomerSummary {
  customerId: string;
  fullName: string;
  email: string;
  phone: string;
  status: 'ENROLLED' | 'DELETED' | 'SUSPENDED';
  enrolledAt: string;
}

export interface CustomerDetail extends CustomerSummary {
  dateOfBirth: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  postcode: string;
  country: string;
  updatedAt: string;
  accountCount: number;
}

export interface CustomerSearchQuery extends PageRequest {
  name?: string;
  accountNumber?: string;
}

function toParams(query: CustomerSearchQuery): Record<string, string | number> {
  const params: Record<string, string | number> = {};
  if (query.page !== undefined) params.page = query.page;
  if (query.size !== undefined) params.size = query.size;
  if (query.sort) params.sort = query.sort;
  if (query.name) params.name = query.name;
  if (query.accountNumber) params.accountNumber = query.accountNumber;
  return params;
}

export const customersApi = {
  search: (
    query: CustomerSearchQuery = {},
    signal?: AbortSignal,
  ): Promise<Page<CustomerSummary>> =>
    api.get<Page<CustomerSummary>>('/customers', { params: toParams(query), signal }),
  get: (customerId: string, signal?: AbortSignal): Promise<CustomerDetail> =>
    api.get<CustomerDetail>(`/customers/${encodeURIComponent(customerId)}`, { signal }),
};
