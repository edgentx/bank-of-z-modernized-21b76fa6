'use client';

import {
  ReactNode,
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useReducer,
} from 'react';
import {
  ANONYMOUS_IDENTITY,
  hasAnyRole,
  hasRole,
  isAuthenticated,
  isSessionExpired,
  msUntilExpiry,
} from './identity';
import { AuthAction, UserIdentity } from './types';

export interface AuthContextValue {
  user: UserIdentity;
  authenticated: boolean;
  hasRole: (role: string) => boolean;
  hasAnyRole: (roles: ReadonlyArray<string>) => boolean;
  logout: () => void;
  reauthenticate: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function reducer(state: UserIdentity, action: AuthAction): UserIdentity {
  switch (action.type) {
    case 'hydrate':
      return action.user;
    case 'logout':
    case 'expire':
      return ANONYMOUS_IDENTITY;
    default: {
      const _exhaustive: never = action;
      return state;
    }
  }
}

export interface AuthProviderProps {
  initialUser: UserIdentity;
  reauthenticateUrl?: string;
  children: ReactNode;
}

const DEFAULT_REAUTH_URL = '/login';

export function AuthProvider({
  initialUser,
  reauthenticateUrl = DEFAULT_REAUTH_URL,
  children,
}: AuthProviderProps) {
  const [user, dispatch] = useReducer(reducer, initialUser);

  const reauthenticate = useCallback(() => {
    if (typeof window !== 'undefined') {
      window.location.assign(reauthenticateUrl);
    }
  }, [reauthenticateUrl]);

  const logout = useCallback(() => {
    dispatch({ type: 'logout' });
    reauthenticate();
  }, [reauthenticate]);

  // Session-timeout watchdog: when the sidecar-supplied expiresAt elapses
  // (or has already elapsed at mount), tear the session down and bounce
  // the user back through the sidecar's authentication flow.
  useEffect(() => {
    if (!isAuthenticated(user)) return;
    const remaining = msUntilExpiry(user, Date.now());
    if (remaining === null) return;
    if (remaining <= 0) {
      dispatch({ type: 'expire' });
      reauthenticate();
      return;
    }
    const timer = setTimeout(() => {
      dispatch({ type: 'expire' });
      reauthenticate();
    }, remaining);
    return () => clearTimeout(timer);
  }, [user, reauthenticate]);

  // Re-check on tab refocus — sleep / suspend can let a setTimeout drift
  // past the real expiry without firing.
  useEffect(() => {
    if (typeof document === 'undefined') return;
    function onVisible() {
      if (document.visibilityState !== 'visible') return;
      if (isAuthenticated(user) && isSessionExpired(user, Date.now())) {
        dispatch({ type: 'expire' });
        reauthenticate();
      }
    }
    document.addEventListener('visibilitychange', onVisible);
    return () => document.removeEventListener('visibilitychange', onVisible);
  }, [user, reauthenticate]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      authenticated: isAuthenticated(user),
      hasRole: (role: string) => hasRole(user, role),
      hasAnyRole: (roles: ReadonlyArray<string>) => hasAnyRole(user, roles),
      logout,
      reauthenticate,
    }),
    [user, logout, reauthenticate],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (ctx === null) {
    throw new Error('useAuth must be used inside <AuthProvider>');
  }
  return ctx;
}

export function useUser(): UserIdentity {
  return useAuth().user;
}
