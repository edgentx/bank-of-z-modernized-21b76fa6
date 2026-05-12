import { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/utils';

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  title: string;
  description?: string;
  footer?: ReactNode;
}

export function Card({ title, description, footer, className, children, ...props }: CardProps) {
  return (
    <div
      className={cn(
        'rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition-shadow hover:shadow-md',
        className,
      )}
      {...props}
    >
      <h3 className="text-lg font-semibold text-teller">{title}</h3>
      {description && <p className="mt-1 text-sm text-slate-600">{description}</p>}
      {children && <div className="mt-3">{children}</div>}
      {footer && <div className="mt-4 border-t border-slate-100 pt-3 text-xs text-slate-500">{footer}</div>}
    </div>
  );
}
