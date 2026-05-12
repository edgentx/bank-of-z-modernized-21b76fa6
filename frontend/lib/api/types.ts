// Shared API envelope types for the Bank-of-Z teller frontend.
// Concrete resource DTOs land in sibling files (accounts.ts, transactions.ts, ...) as
// their command-handler stories ship.

export interface ApiErrorBody {
  code: string;
  message: string;
  details?: Record<string, unknown>;
}

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;
  readonly details?: Record<string, unknown>;

  constructor(status: number, body: ApiErrorBody) {
    super(body.message);
    this.name = 'ApiError';
    this.status = status;
    this.code = body.code;
    this.details = body.details;
  }
}

export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string;
}

export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}
