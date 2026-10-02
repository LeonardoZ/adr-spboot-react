import { afterEach, describe, expect, it, vi } from 'vitest';
import { adlApi } from './adlApi';

const adl = {
  identifier: 'ADL-1',
  title: 'Ledger',
  context: 'Context',
  problem: 'Problem',
  archivedAt: null,
  tags: [],
  adrs: [],
  version: 0,
};

describe('ADL API client', () => {
  afterEach(() => vi.unstubAllGlobals());

  it('uses list, edit, and archive endpoints with optimistic versions', async () => {
    const fetchMock = vi.fn().mockResolvedValue({ ok: true, json: async () => adl });
    vi.stubGlobal('fetch', fetchMock);
    await adlApi.list('token', { text: 'ledger' });
    await adlApi.detail('token', 'ADL-1');
    await adlApi.create('token', { ...adl, tags: ['finance'] });
    await adlApi.update('token', 'ADL-1', { ...adl, title: 'Updated', tags: [] });
    await adlApi.archive('token', 'ADL-1', 0);
    expect(fetchMock.mock.calls.map(([url, init]) => [url, init?.method])).toEqual([
      ['/api/adls?text=ledger', undefined],
      ['/api/adls/ADL-1', undefined],
      ['/api/adls', 'POST'],
      ['/api/adls/ADL-1', 'PUT'],
      ['/api/adls/ADL-1/archive', 'POST'],
    ]);
  });

  it('surfaces server validation and conflict messages', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        json: async () => ({ message: 'ADL was modified by another user' }),
      }),
    );
    await expect(adlApi.archive('token', 'ADL-1', 0)).rejects.toThrow(
      'ADL was modified by another user',
    );
  });
});
