// Server-only helper to read the trusted identity headers off the
// incoming request. Runs inside React Server Components / Route Handlers
// only — calling it from a client component is a runtime error in Next.

import { headers } from 'next/headers';
import { ANONYMOUS_IDENTITY, identityFromHeaders, isAuthenticated } from './identity';
import { readMockIdentity } from './mock-identity';
import { UserIdentity } from './types';

export function readTrustedIdentity(): UserIdentity {
  let trustedUser = ANONYMOUS_IDENTITY;
  try {
    const headerList = headers();
    trustedUser = identityFromHeaders((name) => headerList.get(name));
  } catch {
    // headers() throws in any non-request context (build-time pre-render,
    // unit test, RSC running outside a request). Fall back to anonymous so
    // the layout stays renderable.
  }
  return isAuthenticated(trustedUser) ? trustedUser : readMockIdentity();
}
