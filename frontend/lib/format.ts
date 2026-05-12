// Display formatters shared across teller views.
//
// Money values arrive from the backend as integer minor units (pence/cents);
// the formatter divides by 100 before delegating to Intl.NumberFormat so the
// presentation layer never invents floating-point arithmetic on currency.

const DEFAULT_LOCALE = 'en-GB';

export function formatMoney(minorUnits: number, currency: string = 'GBP'): string {
  if (!Number.isFinite(minorUnits)) return '—';
  const value = minorUnits / 100;
  try {
    return new Intl.NumberFormat(DEFAULT_LOCALE, {
      style: 'currency',
      currency,
      currencyDisplay: 'narrowSymbol',
    }).format(value);
  } catch {
    return `${value.toFixed(2)} ${currency}`;
  }
}

export function formatCount(value: number): string {
  if (!Number.isFinite(value)) return '—';
  return new Intl.NumberFormat(DEFAULT_LOCALE).format(value);
}

export function formatDateTime(iso: string): string {
  const ms = Date.parse(iso);
  if (!Number.isFinite(ms)) return iso;
  return new Intl.DateTimeFormat(DEFAULT_LOCALE, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(ms));
}

export function formatDate(iso: string): string {
  const ms = Date.parse(iso);
  if (!Number.isFinite(ms)) return iso;
  return new Intl.DateTimeFormat(DEFAULT_LOCALE, { dateStyle: 'medium' }).format(new Date(ms));
}
