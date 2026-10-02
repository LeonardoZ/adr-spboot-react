import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AdrDetailPage, AdrFormPage, AdrsPage } from './AdrsPage';

const mocks = vi.hoisted(() => ({
  list: vi.fn(),
  detail: vi.fn(),
  create: vi.fn(),
  update: vi.fn(),
  submit: vi.fn(),
  cancelReview: vi.fn(),
  approve: vi.fn(),
  reject: vi.fn(),
}));
const auth = vi.hoisted(() => ({ subject: 'architect' }));
vi.mock('../auth/AuthProvider', () => ({
  useAuth: () => ({ user: { access_token: 'token', profile: { sub: auth.subject } } }),
}));
vi.mock('./adrApi', () => ({ adrApi: mocks }));
vi.mock('../audit/AuditHistory', () => ({ AuditHistory: () => <div>Audit history</div> }));
const terminalAdr = {
  identifier: 'ADR-1',
  adlIdentifier: 'ADL-1',
  title: 'Ledger',
  context: 'Context',
  problem: 'Problem',
  status: 'APPROVED',
  authorUserId: 'architect',
  authorDisplayName: 'Alice',
  createdAt: '2026-01-01T00:00:00Z',
  version: 1,
};
const underReviewAdr = { ...terminalAdr, status: 'UNDER_REVIEW' as const };
function renderPage(element: React.ReactNode, route: string) {
  return render(
    <QueryClientProvider
      client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
    >
      <MemoryRouter initialEntries={[route]}>
        <Routes>
          <Route path="/adls/:adlIdentifier/adrs" element={element} />
          <Route path="/adls/:adlIdentifier/adrs/new" element={element} />
          <Route path="/adrs/:identifier" element={element} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe('ADR views', () => {
  beforeEach(() => {
    cleanup();
    vi.clearAllMocks();
    auth.subject = 'architect';
  });
  it('renders an actionable Architecture Decision Records empty state', async () => {
    mocks.list.mockResolvedValueOnce([]);
    renderPage(<AdrsPage />, '/adls/ADL-1/adrs');
    expect(await screen.findByText('No Architecture Decision Records yet')).toBeInTheDocument();
    expect(
      screen
        .getAllByRole('link', { name: 'Create ADR' })
        .every((link) => link.getAttribute('href') === '/adls/ADL-1/adrs/new'),
    ).toBe(true);
  });
  it('validates all required decision content before creating', async () => {
    renderPage(<AdrFormPage />, '/adls/ADL-1/adrs/new');
    await userEvent.click(await screen.findByRole('button', { name: 'Save ADR' }));
    expect(await screen.findByText('Title is required')).toBeInTheDocument();
    expect(mocks.create).not.toHaveBeenCalled();
  });
  it('renders terminal ADRs as read-only', async () => {
    mocks.detail.mockResolvedValueOnce(terminalAdr);
    renderPage(<AdrDetailPage />, '/adrs/ADR-1');
    await waitFor(() =>
      expect(screen.getByText('This ADR is read-only in its APPROVED state.')).toBeInTheDocument(),
    );
    expect(screen.queryByText('Edit')).not.toBeInTheDocument();
  });
  it('shows a USER the available decision actions and submits a rejection justification', async () => {
    mocks.detail.mockResolvedValue(underReviewAdr);
    mocks.reject.mockResolvedValue(underReviewAdr);
    const user = userEvent.setup();
    renderPage(<AdrDetailPage />, '/adrs/ADR-1');
    await screen.findByRole('button', { name: 'Approve' });
    expect(screen.getByRole('link', { name: 'ADL-1' })).toHaveAttribute('href', '/adls/ADL-1');
    expect(screen.getByRole('button', { name: 'Reject' })).toBeDisabled();
    await user.type(screen.getByLabelText('Decision comment'), 'Does not meet the constraints');
    await user.type(screen.getByLabelText(/Rejection justification/), 'Operational risk');
    await user.click(screen.getByRole('button', { name: 'Reject' }));
    await waitFor(() =>
      expect(mocks.reject).toHaveBeenCalledWith(
        'token',
        'ADR-1',
        1,
        'Does not meet the constraints',
        'Operational risk',
      ),
    );
  });
  it('retains author-only review cancellation while exposing decisions to a USER', async () => {
    auth.subject = 'reader';
    mocks.detail.mockResolvedValue(underReviewAdr);
    renderPage(<AdrDetailPage />, '/adrs/ADR-1');
    await screen.findByText('UNDER REVIEW');
    expect(screen.getByRole('button', { name: 'Approve' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Cancel review' })).not.toBeInTheDocument();
  });

  it('presents workflow mutation errors', async () => {
    mocks.detail.mockResolvedValue(underReviewAdr);
    mocks.approve.mockRejectedValueOnce(new Error('Decision was modified by another approver'));
    renderPage(<AdrDetailPage />, '/adrs/ADR-1');
    await userEvent.click(await screen.findByRole('button', { name: 'Approve' }));
    expect(
      await screen.findByText('Decision was modified by another approver'),
    ).toBeInTheDocument();
  });
});
