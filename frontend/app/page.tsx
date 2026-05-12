import Link from 'next/link';
import { Card } from '@/components/ui/card';

const tiles: ReadonlyArray<{ href: '/accounts' | '/customers' | '/transactions'; title: string; blurb: string }> = [
  { href: '/accounts', title: 'Accounts', blurb: 'Open, close, and inspect customer accounts.' },
  { href: '/customers', title: 'Customers', blurb: 'Manage customer records and KYC details.' },
  { href: '/transactions', title: 'Transactions', blurb: 'Post deposits, withdrawals, and transfers.' },
];

export default function HomePage() {
  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-3xl font-semibold text-teller">Teller Workstation</h1>
        <p className="mt-2 text-slate-600">
          Modernized front-end for the Bank-of-Z core. Wired to the Spring Boot teller-core service
          via the typed API client in <code>lib/api/client.ts</code>.
        </p>
      </header>
      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {tiles.map((tile) => (
          <Link key={tile.href} href={tile.href} className="block focus:outline-none">
            <Card title={tile.title} description={tile.blurb} />
          </Link>
        ))}
      </section>
    </div>
  );
}
