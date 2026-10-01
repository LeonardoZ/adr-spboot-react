import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, expect, it, afterEach } from 'vitest';
import { AdlDetailPage, AdlFormPage, AdlsPage } from './AdlsPage';

const mocks = vi.hoisted(() => ({ list: vi.fn(), detail: vi.fn(), create: vi.fn(), update: vi.fn(), archive: vi.fn() }));
vi.mock('../auth/AuthProvider', () => ({ useAuth: () => ({ user: { access_token: 'token' } }) }));
vi.mock('./adlApi', () => ({ adlApi: mocks }));
const adl = { identifier: 'ADL-1', title: 'Ledger', context: 'Context', problem: 'Problem', archivedAt: '2026-01-01T00:00:00Z', tags: [], adrs: [], version: 0 };
afterEach(() => { cleanup(); vi.clearAllMocks(); });
function renderPage(element: React.ReactNode, route = '/adls') { return render(<QueryClientProvider client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}><MemoryRouter initialEntries={[route]}><Routes><Route path="/adls" element={element}/><Route path="/adls/new" element={element}/><Route path="/adls/:identifier" element={element}/></Routes></MemoryRouter></QueryClientProvider>); }

describe('ADL views', () => {
  it('renders an actionable empty filter result', async () => { mocks.list.mockResolvedValueOnce({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 }); renderPage(<AdlsPage />); expect(await screen.findByText('No matching decision logs')).toBeInTheDocument(); expect(screen.getAllByRole('link', { name: 'Create ADL' }).every((link) => link.getAttribute('href') === '/adls/new')).toBe(true); });
  it('shows form validation before creating an ADL', async () => { renderPage(<AdlFormPage />, '/adls/new'); await userEvent.click(await screen.findByRole('button', { name: 'Save ADL' })); expect(await screen.findByText('Title is required')).toBeInTheDocument(); expect(mocks.create).not.toHaveBeenCalled(); });
  it('renders archived ADLs as read-only while allowing ADR management', async () => { mocks.detail.mockResolvedValueOnce(adl); renderPage(<AdlDetailPage />, '/adls/ADL-1'); await waitFor(() => expect(screen.getByText('This ADL is archived and read-only.')).toBeInTheDocument()); expect(screen.getByRole('link', { name: 'Manage ADRs' })).toHaveAttribute('href', '/adls/ADL-1/adrs'); expect(screen.queryByRole('link', { name: 'Create ADR' })).not.toBeInTheDocument(); expect(screen.queryByText(/Alternatives/)).not.toBeInTheDocument(); expect(screen.queryByText('Edit')).not.toBeInTheDocument(); });
  it('offers ADR management and creation from an active ADL detail', async () => { mocks.detail.mockResolvedValueOnce({ ...adl, archivedAt: null }); renderPage(<AdlDetailPage />, '/adls/ADL-1'); expect(await screen.findByRole('link', { name: 'Manage ADRs' })).toHaveAttribute('href', '/adls/ADL-1/adrs'); expect(screen.getByRole('link', { name: 'Create ADR' })).toHaveAttribute('href', '/adls/ADL-1/adrs/new'); expect(screen.queryByText(/Alternatives/)).not.toBeInTheDocument(); });
  it('links to ADR management without rendering an Alternatives summary', async () => {
    mocks.detail.mockResolvedValueOnce({ ...adl, adrs: [{ identifier: 'ADR-1', title: 'Use ledger', status: 'APPROVED', authorDisplayName: 'Alice', createdAt: '2026-01-01T00:00:00Z' }] });
    renderPage(<AdlDetailPage />, '/adls/ADL-1');
    expect(await screen.findByRole('link', { name: 'Manage ADRs' })).toHaveAttribute('href', '/adls/ADL-1/adrs');
    expect(screen.queryByText('Use ledger')).not.toBeInTheDocument();
    expect(screen.queryByText(/Alternatives/)).not.toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Decision Logs' })).toHaveAttribute('href', '/adls');
  });
  it('does not ask for a project when creating an ADL', async () => {
    renderPage(<AdlFormPage />, '/adls/new');
    expect(await screen.findByRole('button', { name: 'Save ADL' })).toBeInTheDocument();
    expect(screen.queryByLabelText('Project')).not.toBeInTheDocument();
  });
});
