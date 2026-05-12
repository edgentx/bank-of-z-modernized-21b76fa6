// Server-only helper to read the trusted identity headers off the
// incoming request. Runs inside React Server Components / Route Handlers
// only — calling it from a client component is a runtime error in Next.

import { headers } from 'next/headers';
import { ANONYMOUS_IDENTITY, identityFromHeaders } from './identity';
import { UserIdentity } from './types';

export function readTrustedIdentity(): UserIdentity {
  try {
    const headerList = headers();
    return identityFromHeaders((name) => headerList.get(name));
  } catch {
    // headers() throws in any non-request context (build-time pre-render,
    // unit test, RSC running outside a request). Fall back to anonymous so
    // the layout stays renderable.
    return ANONYMOUS_IDENTITY;
  }
}
