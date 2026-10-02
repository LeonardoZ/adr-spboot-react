import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { AuditHistory } from './AuditHistory';

const mocks = vi.hoisted(() => ({ adl: vi.fn(), adr: vi.fn() }));
vi.mock('../auth/AuthProvider', () => ({ useAuth: () => ({ user: { access_token: 'token' } }) }));
vi.mock('./auditApi', () => ({ auditApi: mocks }));

function renderHistory() {
  return render(
    <QueryClientProvider
      client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
    >
      <AuditHistory kind="adl" identifier="ADL-1" />
    </QueryClientProvider>,
  );
}

describe('AuditHistory', () => {
  it('renders structured chronological event values', async () => {
    mocks.adl.mockResolvedValueOnce([
      {
        action: 'CREATED',
        actorDisplayName: 'Architect One',
        occurredAt: '2026-01-01T10:00:00Z',
        afterValue: '{"status":"OPEN"}',
      },
      {
        action: 'ARCHIVED',
        actorDisplayName: 'Architect Two',
        occurredAt: '2026-02-01T10:00:00Z',
        afterValue: '{"status":"ARCHIVED"}',
      },
    ]);
    renderHistory();
    expect(await screen.findByText('CREATED')).toBeInTheDocument();
    expect(screen.getByText('by Architect One')).toBeInTheDocument();
    expect(screen.getByText('{"status":"OPEN"}')).toBeInTheDocument();
    expect(screen.getByText('ARCHIVED')).toBeInTheDocument();
    expect(screen.getByText('by Architect Two')).toBeInTheDocument();
  });

  it('renders an empty-history message', async () => {
    mocks.adl.mockResolvedValueOnce([]);
    renderHistory();
    expect(await screen.findByText('No audit events have been recorded.')).toBeInTheDocument();
  });
});
