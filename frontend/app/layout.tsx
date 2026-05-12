import type { Metadata } from 'next';
import { ReactNode } from 'react';
import { Nav } from '@/components/layout/nav';
import './globals.css';

export const metadata: Metadata = {
  title: 'Bank-of-Z Teller',
  description: 'Modern teller workstation for the Bank-of-Z modernization platform.',
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>
        <Nav />
        <main className="mx-auto max-w-6xl px-6 py-10">{children}</main>
      </body>
    </html>
  );
}
