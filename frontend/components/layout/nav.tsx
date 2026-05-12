'use client';

import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/lib/auth/context';

const links: ReadonlyArray<{
  href: '/' | '/accounts' | '/customers' | '/transactions';
  label: string;
}> = [
  { href: '/', label: 'Home' },
  { href: '/accounts', label: 'Accounts' },
  { href: '/customers', label: 'Customers' },
  { href: '/transactions', label: 'Transactions' },
];

export function Nav() {
  const auth = useAuth();
  return (
    <nav className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-6 px-6 py-4">
        <span className="text-lg font-semibold text-teller">Bank-of-Z Teller</span>
        <ul className="flex items-center gap-6 text-sm text-slate-700">
          {links.map((link) => (
            <li key={link.href}>
              <Link href={link.href} className="hover:text-teller-accent">
                {link.label}
              </Link>
            </li>
          ))}
        </ul>
        <div className="flex items-center gap-3 text-sm">
          {auth.authenticated ? (
            <>
              <span className="text-slate-600" data-testid="auth-identity">
                <span className="font-medium text-teller">{auth.user.userId}</span>
                {auth.user.roles.length > 0 && (
                  <span className="ml-2 text-slate-500">({auth.user.roles.join(', ')})</span>
                )}
              </span>
              <Button variant="secondary" onClick={auth.logout}>
                Log out
              </Button>
            </>
          ) : (
            <Link href="/login" className="hover:text-teller-accent">
              Sign in
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}
