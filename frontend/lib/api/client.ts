import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import { ApiError, ApiErrorBody } from './types';

const DEFAULT_TIMEOUT_MS = 15_000;

function readBaseUrl(): string {
  const fromEnv = process.env.NEXT_PUBLIC_API_BASE_URL;
  if (fromEnv && fromEnv.length > 0) {
    return fromEnv.replace(/\/+$/, '');
  }
  return 'http://localhost:8080/api';
}

function readTimeout(): number {
  const raw = process.env.API_REQUEST_TIMEOUT_MS;
  if (!raw) return DEFAULT_TIMEOUT_MS;
  const parsed = Number.parseInt(raw, 10);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : DEFAULT_TIMEOUT_MS;
}

function toApiError(err: unknown): ApiError {
  if (axios.isAxiosError(err)) {
    const axiosErr = err as AxiosError<ApiErrorBody>;
    const status = axiosErr.response?.status ?? 0;
    const body =
      axiosErr.response?.data ?? {
        code: axiosErr.code ?? 'NETWORK_ERROR',
        message: axiosErr.message,
      };
    return new ApiError(status, body);
  }
  const message = err instanceof Error ? err.message : String(err);
  return new ApiError(0, { code: 'UNKNOWN', message });
}

export function createApiClient(overrides: AxiosRequestConfig = {}): AxiosInstance {
  const instance = axios.create({
    baseURL: readBaseUrl(),
    timeout: readTimeout(),
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    ...overrides,
  });

  instance.interceptors.response.use(
    (res) => res,
    (err: unknown) => Promise.reject(toApiError(err)),
  );

  return instance;
}

export const apiClient: AxiosInstance = createApiClient();

export interface RequestOptions extends AxiosRequestConfig {
  signal?: AbortSignal;
}

async function unwrap<T>(promise: Promise<{ data: T }>): Promise<T> {
  const { data } = await promise;
  return data;
}

export const api = {
  get: <T>(url: string, options: RequestOptions = {}): Promise<T> =>
    unwrap<T>(apiClient.get<T>(url, options)),
  post: <T, B = unknown>(url: string, body?: B, options: RequestOptions = {}): Promise<T> =>
    unwrap<T>(apiClient.post<T>(url, body, options)),
  put: <T, B = unknown>(url: string, body?: B, options: RequestOptions = {}): Promise<T> =>
    unwrap<T>(apiClient.put<T>(url, body, options)),
  patch: <T, B = unknown>(url: string, body?: B, options: RequestOptions = {}): Promise<T> =>
    unwrap<T>(apiClient.patch<T>(url, body, options)),
  delete: <T>(url: string, options: RequestOptions = {}): Promise<T> =>
    unwrap<T>(apiClient.delete<T>(url, options)),
};

export { ApiError } from './types';
export type { ApiErrorBody, Page, PageRequest } from './types';
