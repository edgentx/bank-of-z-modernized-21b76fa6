import { cn } from '@/lib/utils';

export interface SpinnerProps {
  className?: string;
  label?: string;
}

export function Spinner({ className, label = 'Loading' }: SpinnerProps) {
  return (
    <span
      role="status"
      aria-live="polite"
      aria-label={label}
      className={cn('inline-flex items-center gap-2 text-sm text-slate-600', className)}
    >
      <span
        aria-hidden="true"
        className="inline-block h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-teller"
      />
      <span className="sr-only">{label}</span>
      <span aria-hidden="true">{label}…</span>
    </span>
  );
}
