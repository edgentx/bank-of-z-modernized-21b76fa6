export {
  ANONYMOUS_USER_ID,
  TRUSTED_USER_ID_HEADER,
  TRUSTED_USER_ROLES_HEADER,
  TRUSTED_SESSION_EXPIRES_HEADER,
} from './types';
export type { AuthAction, AuthSnapshot, UserIdentity } from './types';

export {
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
} from './identity';

export { AuthProvider, useAuth, useUser } from './context';
export type { AuthContextValue, AuthProviderProps } from './context';
