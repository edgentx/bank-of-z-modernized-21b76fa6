import { strict as assert } from 'node:assert';
import { afterEach, test } from 'node:test';
import { ANONYMOUS_IDENTITY } from '../identity';
import { readMockIdentity } from '../mock-identity';

const ENV_KEYS = ['BANK_MOCK_USER_ENABLED', 'BANK_MOCK_USER_ID', 'BANK_MOCK_USER_ROLES'];

afterEach(() => {
  for (const key of ENV_KEYS) {
    delete process.env[key];
  }
});

test('mock identity provides a default teller when enabled by default', () => {
  const user = readMockIdentity();

  assert.equal(user.userId, 'TELLER001');
  assert.deepEqual(user.roles, ['TELLER', 'SUPERVISOR']);
  assert.equal(user.expiresAt, null);
});

test('mock identity can be customized by environment variables', () => {
  process.env.BANK_MOCK_USER_ID = 'BRANCH99';
  process.env.BANK_MOCK_USER_ROLES = 'BRANCH_MANAGER';

  const user = readMockIdentity();

  assert.equal(user.userId, 'BRANCH99');
  assert.deepEqual(user.roles, ['BRANCH_MANAGER']);
});

test('mock identity can be disabled for real trusted-header auth', () => {
  process.env.BANK_MOCK_USER_ENABLED = 'false';

  assert.deepEqual(readMockIdentity(), ANONYMOUS_IDENTITY);
});
