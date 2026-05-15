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

export interface SpringPage<T> {
  content?: T[];
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}

export type PageLike<T> = Page<T> | SpringPage<T>;

export function normalizePage<T>(page: PageLike<T>): Page<T> {
  const source = page as Page<T> & SpringPage<T>;
  const items = Array.isArray(source.items)
    ? source.items
    : Array.isArray(source.content)
      ? source.content
      : [];

  return {
    items,
    page: typeof source.page === 'number' ? source.page : source.number ?? 0,
    size: source.size ?? items.length,
    totalItems:
      typeof source.totalItems === 'number'
        ? source.totalItems
        : source.totalElements ?? items.length,
    totalPages: source.totalPages ?? 0,
  };
}
