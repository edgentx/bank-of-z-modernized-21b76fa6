import { strict as assert } from 'node:assert';
import { test } from 'node:test';
import { mapKey, shouldConsume } from '../keymap';

test('Tab without modifier moves to the next field', () => {
  assert.equal(mapKey({ key: 'Tab' }), 'NEXT_FIELD');
  assert.equal(mapKey({ key: 'Tab', shiftKey: false }), 'NEXT_FIELD');
});

test('Shift+Tab moves to the previous field', () => {
  assert.equal(mapKey({ key: 'Tab', shiftKey: true }), 'PREV_FIELD');
});

test('Enter submits the screen (AID=ENTER)', () => {
  assert.equal(mapKey({ key: 'Enter' }), 'SUBMIT');
});

test('F3 exits / returns to previous screen', () => {
  assert.equal(mapKey({ key: 'F3' }), 'EXIT');
});

test('Escape and Pause act as CLEAR', () => {
  assert.equal(mapKey({ key: 'Escape' }), 'CLEAR');
  assert.equal(mapKey({ key: 'Pause' }), 'CLEAR');
});

test('Modifier-augmented keys fall through so browser shortcuts work', () => {
  assert.equal(mapKey({ key: 'Tab', ctrlKey: true }), 'NONE');
  assert.equal(mapKey({ key: 'Enter', metaKey: true }), 'NONE');
  assert.equal(mapKey({ key: 'F3', altKey: true }), 'NONE');
});

test('Unrelated keys produce NONE', () => {
  assert.equal(mapKey({ key: 'a' }), 'NONE');
  assert.equal(mapKey({ key: 'ArrowLeft' }), 'NONE');
  assert.equal(mapKey({ key: 'F5' }), 'NONE');
});

test('shouldConsume is true for every recognised action, false for NONE', () => {
  for (const action of ['NEXT_FIELD', 'PREV_FIELD', 'SUBMIT', 'EXIT', 'CLEAR'] as const) {
    assert.equal(shouldConsume(action), true, `${action} should be consumed`);
  }
  assert.equal(shouldConsume('NONE'), false);
});
