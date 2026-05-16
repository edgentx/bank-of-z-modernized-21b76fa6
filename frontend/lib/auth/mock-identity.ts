import { ANONYMOUS_IDENTITY, parseExpiresAt, parseRoles } from './identity';
import { UserIdentity } from './types';

const DEFAULT_MOCK_USER_ID = 'TELLER001';
const DEFAULT_MOCK_ROLES = 'TELLER,SUPERVISOR';

export function isMockIdentityEnabled(): boolean {
  return process.env.BANK_MOCK_USER_ENABLED !== 'false';
}

export function readMockIdentity(): UserIdentity {
  if (!isMockIdentityEnabled()) return ANONYMOUS_IDENTITY;

  const userId = (process.env.BANK_MOCK_USER_ID || DEFAULT_MOCK_USER_ID).trim();
  if (userId.length === 0) return ANONYMOUS_IDENTITY;

  return {
    userId,
    roles: parseRoles(process.env.BANK_MOCK_USER_ROLES || DEFAULT_MOCK_ROLES),
    expiresAt: parseExpiresAt(process.env.BANK_MOCK_SESSION_EXPIRES_AT),
  };
}
