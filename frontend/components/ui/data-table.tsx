import { ReactNode } from 'react';
import { cn } from '@/lib/utils';

export interface DataTableColumn<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
  align?: 'left' | 'right' | 'center';
  headerSrOnly?: boolean;
}

export interface DataTableProps<T> {
  caption: string;
  columns: ReadonlyArray<DataTableColumn<T>>;
  rows: ReadonlyArray<T>;
  rowKey: (row: T) => string;
  emptyMessage?: string;
  onRowSelect?: (row: T) => void;
  selectedRowKey?: string;
  className?: string;
  tableClassName?: string;
}

const alignClass: Record<NonNullable<DataTableColumn<unknown>['align']>, string> = {
  left: 'text-left',
  right: 'text-right',
  center: 'text-center',
};

export function DataTable<T>({
  caption,
  columns,
  rows,
  rowKey,
  emptyMessage = 'No results.',
  onRowSelect,
  selectedRowKey,
  className,
  tableClassName,
}: DataTableProps<T>) {
  const interactive = Boolean(onRowSelect);
  return (
    <div className={cn('overflow-x-auto rounded-md border border-slate-200 bg-white', className)}>
      <table className={cn('w-full border-collapse text-sm', tableClassName)}>
        <caption className="sr-only">{caption}</caption>
        <thead className="bg-slate-50 text-slate-700">
          <tr>
            {columns.map((col) => (
              <th
                key={col.key}
                scope="col"
                className={cn(
                  'px-4 py-2 font-semibold',
                  alignClass[col.align ?? 'left'],
                  col.headerSrOnly && 'sr-only',
                )}
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {rows.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="px-4 py-6 text-center text-slate-500">
                {emptyMessage}
              </td>
            </tr>
          ) : (
            rows.map((row) => {
              const key = rowKey(row);
              const selected = selectedRowKey === key;
              return (
                <tr
                  key={key}
                  aria-selected={interactive ? selected : undefined}
                  className={cn(
                    'text-slate-800',
                    interactive &&
                      'cursor-pointer focus-within:bg-teller-surface hover:bg-teller-surface',
                    selected && 'bg-teller-surface',
                  )}
                  onClick={onRowSelect ? () => onRowSelect(row) : undefined}
                  onKeyDown={
                    onRowSelect
                      ? (event) => {
                          if (event.key === 'Enter' || event.key === ' ') {
                            event.preventDefault();
                            onRowSelect(row);
                          }
                        }
                      : undefined
                  }
                  tabIndex={interactive ? 0 : undefined}
                  role={interactive ? 'button' : undefined}
                >
                  {columns.map((col) => (
                    <td
                      key={col.key}
                      className={cn('px-4 py-3 align-middle', alignClass[col.align ?? 'left'])}
                    >
                      {col.render(row)}
                    </td>
                  ))}
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
}
