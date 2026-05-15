import { strict as assert } from 'node:assert';
import { test } from 'node:test';
import { normalizePage } from '../types';

test('normalizePage preserves frontend page envelopes', () => {
  const page = normalizePage({
    items: [{ accountId: 'a-1' }],
    page: 2,
    size: 10,
    totalItems: 21,
    totalPages: 3,
  });

  assert.deepEqual(page, {
    items: [{ accountId: 'a-1' }],
    page: 2,
    size: 10,
    totalItems: 21,
    totalPages: 3,
  });
});

test('normalizePage maps Spring page envelopes', () => {
  const page = normalizePage({
    content: [{ accountId: 'a-1' }],
    number: 1,
    size: 25,
    totalElements: 26,
    totalPages: 2,
  });

  assert.deepEqual(page, {
    items: [{ accountId: 'a-1' }],
    page: 1,
    size: 25,
    totalItems: 26,
    totalPages: 2,
  });
});
