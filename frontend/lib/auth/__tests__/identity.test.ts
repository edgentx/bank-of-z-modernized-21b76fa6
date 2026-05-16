import { strict as assert } from 'node:assert';
import { test } from 'node:test';
import {
  ANONYMOUS_IDENTITY,
  hasAnyRole,
  hasRole,
  identityFromHeaders,
  isAuthenticated,
  isSessionExpired,
  msUntilExpiry,
  parseExpiresAt,
  parseRoles,
  snapshot,
} from '../identity';
import {
  ANONYMOUS_USER_ID,
  TRUSTED_SESSION_EXPIRES_HEADER,
  TRUSTED_USER_ID_HEADER,
  TRUSTED_USER_ROLES_HEADER,
} from '../types';

function reader(headers: Record<string, string | undefined>) {
  return (name: string) => headers[name.toLowerCase()];
}

test('parseRoles splits, trims, and drops empties', () => {
  assert.deepEqual(parseRoles(' teller , supervisor ,, admin '), ['teller', 'supervisor', 'admin']);
});

test('parseRoles returns empty array for null/undefined/empty', () => {
  assert.deepEqual(parseRoles(undefined), []);
  assert.deepEqual(parseRoles(null), []);
  assert.deepEqual(parseRoles(''), []);
  assert.deepEqual(
    parseRoles('   '),
    [' '.trim()].filter((s) => s.length > 0),
  );
});

test('parseExpiresAt accepts unix seconds, ms, and ISO-8601', () => {
  assert.equal(parseExpiresAt('1700000000'), 1_700_000_000_000);
  assert.equal(parseExpiresAt('1700000000000'), 1_700_000_000_000);
  assert.equal(parseExpiresAt('2026-05-12T12:00:00Z'), Date.parse('2026-05-12T12:00:00Z'));
  assert.equal(parseExpiresAt(undefined), null);
  assert.equal(parseExpiresAt(''), null);
  assert.equal(parseExpiresAt('not-a-date'), null);
});

test('identityFromHeaders builds UserIdentity from trusted header bundle', () => {
  const user = identityFromHeaders(
    reader({
      [TRUSTED_USER_ID_HEADER]: 'alice',
      [TRUSTED_USER_ROLES_HEADER]: 'teller,supervisor',
      [TRUSTED_SESSION_EXPIRES_HEADER]: '1700000000',
    }),
  );
  assert.equal(user.userId, 'alice');
  assert.deepEqual(user.roles, ['teller', 'supervisor']);
  assert.equal(user.expiresAt, 1_700_000_000_000);
  assert.equal(isAuthenticated(user), true);
});

test('identityFromHeaders falls back to anonymous when X-User-Id is missing or blank', () => {
  assert.deepEqual(identityFromHeaders(reader({})), ANONYMOUS_IDENTITY);
  assert.deepEqual(
    identityFromHeaders(reader({ [TRUSTED_USER_ID_HEADER]: '   ' })),
    ANONYMOUS_IDENTITY,
  );
  assert.equal(ANONYMOUS_IDENTITY.userId, ANONYMOUS_USER_ID);
  assert.equal(isAuthenticated(ANONYMOUS_IDENTITY), false);
});

test('hasRole / hasAnyRole inspect identity.roles', () => {
  const user = identityFromHeaders(
    reader({
      [TRUSTED_USER_ID_HEADER]: 'bob',
      [TRUSTED_USER_ROLES_HEADER]: 'teller',
    }),
  );
  assert.equal(hasRole(user, 'teller'), true);
  assert.equal(hasRole(user, 'TELLER'), true);
  assert.equal(hasRole(user, 'admin'), false);
  assert.equal(hasAnyRole(user, ['admin', 'teller']), true);
  assert.equal(hasAnyRole(user, ['admin', 'TELLER']), true);
  assert.equal(hasAnyRole(user, ['admin', 'auditor']), false);
  assert.equal(hasAnyRole(user, []), true, 'empty required-set is vacuously satisfied');
});

test('session expiry: isSessionExpired and msUntilExpiry honor expiresAt', () => {
  const expired = identityFromHeaders(
    reader({
      [TRUSTED_USER_ID_HEADER]: 'carol',
      [TRUSTED_SESSION_EXPIRES_HEADER]: '1000',
    }),
  );
  assert.equal(isSessionExpired(expired, 2_000_000), true);
  assert.equal(msUntilExpiry(expired, 2_000_000), 0);

  const future = identityFromHeaders(
    reader({
      [TRUSTED_USER_ID_HEADER]: 'dave',
      [TRUSTED_SESSION_EXPIRES_HEADER]: '2000',
    }),
  );
  // 2000s = 2_000_000ms; now=500ms → remaining=1_999_500
  assert.equal(isSessionExpired(future, 500), false);
  assert.equal(msUntilExpiry(future, 500), 1_999_500);
});

test('no expiry header => session never expires from the frontend POV', () => {
  const user = identityFromHeaders(reader({ [TRUSTED_USER_ID_HEADER]: 'eve' }));
  assert.equal(user.expiresAt, null);
  assert.equal(isSessionExpired(user, Number.MAX_SAFE_INTEGER), false);
  assert.equal(msUntilExpiry(user, Number.MAX_SAFE_INTEGER), null);
});

test('snapshot exposes authenticated boolean alongside identity', () => {
  const anon = snapshot(ANONYMOUS_IDENTITY);
  assert.equal(anon.authenticated, false);
  assert.equal(anon.user.userId, ANONYMOUS_USER_ID);

  const signedIn = snapshot(identityFromHeaders(reader({ [TRUSTED_USER_ID_HEADER]: 'frank' })));
  assert.equal(signedIn.authenticated, true);
  assert.equal(signedIn.user.userId, 'frank');
});
