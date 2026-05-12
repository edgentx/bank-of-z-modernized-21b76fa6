import { ButtonHTMLAttributes, forwardRef } from 'react';
import { cn } from '@/lib/utils';

type Variant = 'primary' | 'secondary' | 'ghost';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

const variantClasses: Record<Variant, string> = {
  primary: 'bg-teller text-white hover:bg-teller-accent focus-visible:ring-teller-accent',
  secondary: 'bg-white text-teller border border-teller hover:bg-teller-surface focus-visible:ring-teller',
  ghost: 'bg-transparent text-teller hover:bg-teller-surface focus-visible:ring-teller',
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { className, variant = 'primary', ...props },
  ref,
) {
  return (
    <button
      ref={ref}
      className={cn(
        'inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium shadow-sm transition-colors',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:opacity-60',
        variantClasses[variant],
        className,
      )}
      {...props}
    />
  );
});
