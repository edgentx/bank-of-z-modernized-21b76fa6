import type { Metadata } from 'next';
import { ReactNode } from 'react';
import { Nav } from '@/components/layout/nav';
import { AuthProvider } from '@/lib/auth/context';
import { readTrustedIdentity } from '@/lib/auth/headers';
import './globals.css';

export const metadata: Metadata = {
  title: 'Bank-of-Z Teller',
  description: 'Modern teller workstation for the Bank-of-Z modernization platform.',
};

export default function RootLayout({ children }: { children: ReactNode }) {
  const user = readTrustedIdentity();
  return (
    <html lang="en">
      <body>
        <AuthProvider initialUser={user}>
          <Nav />
          <main className="mx-auto max-w-6xl px-6 py-10">{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
