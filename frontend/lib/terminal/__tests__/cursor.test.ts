import { strict as assert } from 'node:assert';
import { test } from 'node:test';
import type { ScreenField } from '@/lib/api/terminal';
import {
  cellOffset,
  firstFieldIndex,
  nextFieldIndex,
  prevFieldIndex,
  unprotectedIndexes,
} from '../cursor';

function field(name: string, isProtected: boolean): ScreenField {
  return {
    name,
    row: 1,
    col: 1,
    length: isProtected ? 0 : 8,
    protected: isProtected,
    highlight: 'NORMAL',
  };
}

const fields: ReadonlyArray<ScreenField> = [
  field('label_user', true),
  field('user_id', false),
  field('label_pwd', true),
  field('password', false),
  field('label_branch', true),
  field('branch_code', false),
];

test('unprotectedIndexes returns input fields in declaration order', () => {
  assert.deepEqual(unprotectedIndexes(fields), [1, 3, 5]);
});

test('firstFieldIndex returns the first navigable field', () => {
  assert.equal(firstFieldIndex(fields), 1);
});

test('firstFieldIndex returns null on a read-only screen', () => {
  const readOnly: ScreenField[] = [field('a', true), field('b', true)];
  assert.equal(firstFieldIndex(readOnly), null);
});

test('Tab cycles forward through unprotected fields and wraps', () => {
  assert.equal(nextFieldIndex(fields, null), 1);
  assert.equal(nextFieldIndex(fields, 1), 3);
  assert.equal(nextFieldIndex(fields, 3), 5);
  assert.equal(nextFieldIndex(fields, 5), 1);
});

test('Tab from a protected field jumps to the first unprotected field', () => {
  assert.equal(nextFieldIndex(fields, 0), 1);
});

test('Shift+Tab cycles backward and wraps', () => {
  assert.equal(prevFieldIndex(fields, null), 5);
  assert.equal(prevFieldIndex(fields, 5), 3);
  assert.equal(prevFieldIndex(fields, 3), 1);
  assert.equal(prevFieldIndex(fields, 1), 5);
});

test('nextFieldIndex / prevFieldIndex return null when no fields are navigable', () => {
  const readOnly: ScreenField[] = [field('a', true)];
  assert.equal(nextFieldIndex(readOnly, null), null);
  assert.equal(prevFieldIndex(readOnly, null), null);
});

test('cellOffset places (row,col) into a row-major grid', () => {
  assert.equal(cellOffset(1, 1, 80), 0);
  assert.equal(cellOffset(1, 80, 80), 79);
  assert.equal(cellOffset(2, 1, 80), 80);
  assert.equal(cellOffset(24, 80, 80), 24 * 80 - 1);
});

test('cellOffset clamps out-of-bounds columns to the grid edge', () => {
  assert.equal(cellOffset(1, 0, 80), 0, 'col<1 clamps to col=1');
  assert.equal(cellOffset(1, 999, 80), 79, 'col>cols clamps to last column');
});
