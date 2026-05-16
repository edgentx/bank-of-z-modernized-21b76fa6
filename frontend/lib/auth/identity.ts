// Pure identity helpers — no React, no Next.js, no browser globals.
//
// Lifted out of the AuthProvider so that the parsing / role-check /
// session-expiry rules can be unit-tested in isolation (see
// tests/auth/identity.test.ts). This mirrors the pure-helper extraction
// pattern used elsewhere in the platform.

import {
  ANONYMOUS_USER_ID,
  AuthSnapshot,
  TRUSTED_SESSION_EXPIRES_HEADER,
  TRUSTED_USER_ID_HEADER,
  TRUSTED_USER_ROLES_HEADER,
  UserIdentity,
} from './types';

export const ANONYMOUS_IDENTITY: UserIdentity = Object.freeze({
  userId: ANONYMOUS_USER_ID,
  roles: Object.freeze([]) as ReadonlyArray<string>,
  expiresAt: null,
});

export function parseRoles(raw: string | null | undefined): ReadonlyArray<string> {
  if (!raw) return [];
  return raw
    .split(',')
    .map((role) => role.trim())
    .filter((role) => role.length > 0);
}

export function parseExpiresAt(raw: string | null | undefined): number | null {
  if (!raw) return null;
  const trimmed = raw.trim();
  if (trimmed.length === 0) return null;
  // Accept either Unix epoch seconds or an ISO-8601 timestamp.
  const numeric = Number(trimmed);
  if (Number.isFinite(numeric) && numeric > 0) {
    // Heuristic: seconds vs ms. Anything below year ~2286 in seconds.
    return numeric < 1e12 ? Math.floor(numeric * 1000) : Math.floor(numeric);
  }
  const parsed = Date.parse(trimmed);
  return Number.isFinite(parsed) ? parsed : null;
}

export function identityFromHeaders(
  read: (name: string) => string | null | undefined,
): UserIdentity {
  const userId = (read(TRUSTED_USER_ID_HEADER) ?? '').trim();
  if (userId.length === 0) return ANONYMOUS_IDENTITY;
  return {
    userId,
    roles: parseRoles(read(TRUSTED_USER_ROLES_HEADER)),
    expiresAt: parseExpiresAt(read(TRUSTED_SESSION_EXPIRES_HEADER)),
  };
}

export function isAuthenticated(user: UserIdentity): boolean {
  return user.userId !== ANONYMOUS_USER_ID && user.userId.length > 0;
}

export function hasRole(user: UserIdentity, role: string): boolean {
  const target = role.toUpperCase();
  return user.roles.some((candidate) => candidate.toUpperCase() === target);
}

export function hasAnyRole(user: UserIdentity, required: ReadonlyArray<string>): boolean {
  if (required.length === 0) return true;
  return required.some((role) => hasRole(user, role));
}

export function isSessionExpired(user: UserIdentity, nowMs: number): boolean {
  if (user.expiresAt === null) return false;
  return nowMs >= user.expiresAt;
}

export function msUntilExpiry(user: UserIdentity, nowMs: number): number | null {
  if (user.expiresAt === null) return null;
  return Math.max(0, user.expiresAt - nowMs);
}

export function snapshot(user: UserIdentity): AuthSnapshot {
  return { user, authenticated: isAuthenticated(user) };
}
