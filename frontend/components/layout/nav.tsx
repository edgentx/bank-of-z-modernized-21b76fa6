import Link from 'next/link';

const links: ReadonlyArray<{ href: '/' | '/accounts' | '/customers' | '/transactions'; label: string }> = [
  { href: '/', label: 'Home' },
  { href: '/accounts', label: 'Accounts' },
  { href: '/customers', label: 'Customers' },
  { href: '/transactions', label: 'Transactions' },
];

export function Nav() {
  return (
    <nav className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <span className="text-lg font-semibold text-teller">Bank-of-Z Teller</span>
        <ul className="flex gap-6 text-sm text-slate-700">
          {links.map((link) => (
            <li key={link.href}>
              <Link href={link.href} className="hover:text-teller-accent">
                {link.label}
              </Link>
            </li>
          ))}
        </ul>
      </div>
    </nav>
  );
}
