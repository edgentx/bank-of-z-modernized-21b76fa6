import { InputHTMLAttributes, forwardRef } from 'react';
import { cn } from '@/lib/utils';

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  hint?: string;
  errorText?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { className, label, hint, errorText, id, ...props },
  ref,
) {
  const inputId = id ?? props.name;
  const hintId = inputId ? `${inputId}-hint` : undefined;
  const errorId = inputId ? `${inputId}-error` : undefined;
  const describedBy = [errorText ? errorId : null, hint ? hintId : null]
    .filter(Boolean)
    .join(' ') || undefined;

  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label htmlFor={inputId} className="text-sm font-medium text-slate-700">
          {label}
        </label>
      )}
      <input
        ref={ref}
        id={inputId}
        aria-invalid={errorText ? true : undefined}
        aria-describedby={describedBy}
        className={cn(
          'rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm',
          'placeholder:text-slate-400',
          'focus:border-teller-accent focus:outline-none focus:ring-2 focus:ring-teller-accent/40',
          'disabled:cursor-not-allowed disabled:bg-slate-100',
          errorText && 'border-red-500 focus:border-red-500 focus:ring-red-500/40',
          className,
        )}
        {...props}
      />
      {hint && !errorText && (
        <p id={hintId} className="text-xs text-slate-500">
          {hint}
        </p>
      )}
      {errorText && (
        <p id={errorId} role="alert" className="text-xs text-red-600">
          {errorText}
        </p>
      )}
    </div>
  );
});
