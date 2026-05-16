'use client';

import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { UserIdentity } from '@/lib/auth/types';
import { useAuth } from '@/lib/auth/context';

export interface LoginViewProps {
  user: UserIdentity;
  alreadyAuthenticated: boolean;
}

export function LoginView({ user, alreadyAuthenticated }: LoginViewProps) {
  const auth = useAuth();
  const active = auth.authenticated ? auth.user : user;

  if (alreadyAuthenticated || auth.authenticated) {
    return (
      <section className="mx-auto max-w-xl">
        <Card title="You are signed in" description={`Identity: ${active.userId}`}>
          <dl className="mt-2 space-y-2 text-sm text-slate-700">
            <div>
              <dt className="inline font-medium">Roles: </dt>
              <dd className="inline">
                {active.roles.length === 0 ? <em>none</em> : active.roles.join(', ')}
              </dd>
            </div>
          </dl>
          <div className="mt-6 flex gap-3">
            <Link href="/" className="inline-flex">
              <Button variant="primary">Continue to workstation</Button>
            </Link>
            <Button variant="secondary" onClick={auth.logout}>
              Log out
            </Button>
          </div>
        </Card>
      </section>
    );
  }

  return (
    <section className="mx-auto max-w-xl">
      <Card
        title="Sign in"
        description="Authentication is handled by the Envoy + OPA sidecar. Continue to be redirected through the identity provider."
      >
        <p className="mt-2 text-sm text-slate-600">
          The teller workstation does not collect credentials directly — your session is established
          upstream and the sidecar forwards <code>X-User-Id</code> and <code>X-User-Roles</code> on
          every request.
        </p>
        <div className="mt-6">
          <Button variant="primary" onClick={auth.reauthenticate}>
            Continue
          </Button>
        </div>
      </Card>
    </section>
  );
}
