// Auth domain types for the Bank-of-Z teller frontend.
//
// Identity is sourced from trusted headers (`X-User-Id`, `X-User-Roles`)
// injected by the Envoy + OPA sidecar in front of the Spring Boot core.
// The frontend NEVER mints these headers itself — it only reads them off
// the incoming request via `next/headers` on the server boundary.

export interface UserIdentity {
  userId: string;
  roles: ReadonlyArray<string>;
  expiresAt: number | null;
}

export const ANONYMOUS_USER_ID = 'anonymous';

export const TRUSTED_USER_ID_HEADER = 'x-user-id';
export const TRUSTED_USER_ROLES_HEADER = 'x-user-roles';
export const TRUSTED_SESSION_EXPIRES_HEADER = 'x-session-expires-at';

export interface AuthSnapshot {
  user: UserIdentity;
  authenticated: boolean;
}

export type AuthAction =
  | { type: 'hydrate'; user: UserIdentity }
  | { type: 'logout' }
  | { type: 'expire' };
