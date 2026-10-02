import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { HomePage } from './HomePage';

const mocks = vi.hoisted(() => ({ list: vi.fn() }));
vi.mock('../auth/AuthProvider', () => ({ useAuth: () => ({ user: { access_token: 'token' } }) }));
vi.mock('../adl/adlApi', () => ({ adlApi: mocks }));
afterEach(() => vi.clearAllMocks());
const renderPage = () =>
  render(
    <QueryClientProvider
      client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
    >
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    </QueryClientProvider>,
  );

describe('home dashboard', () => {
  it('summarizes and links recent ADLs using the bounded creation-date query', async () => {
    mocks.list.mockResolvedValueOnce({
      content: [{ identifier: 'ADL-1', title: 'Payments' }],
      totalElements: 3,
    });
    renderPage();
    expect(
      await screen.findByText('3 architecture decision logs in the register'),
    ).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Payments ADL-1' })).toHaveAttribute(
      'href',
      '/adls/ADL-1',
    );
    expect(screen.getByRole('link', { name: 'View all logs' })).toHaveAttribute('href', '/adls');
    expect(screen.getByRole('link', { name: 'Create ADL' })).toHaveAttribute('href', '/adls/new');
    expect(mocks.list).toHaveBeenCalledWith('token', { size: '5', sort: 'createdAt,desc' });
  });

  it('keeps an empty register actionable', async () => {
    mocks.list.mockResolvedValueOnce({ content: [], totalElements: 0 });
    renderPage();
    expect(await screen.findByText('Start the conversation')).toBeInTheDocument();
    expect(
      screen
        .getAllByRole('link', { name: 'Create ADL' })
        .every((link) => link.getAttribute('href') === '/adls/new'),
    ).toBe(true);
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });

  it('shows an error when the register cannot load', async () => {
    mocks.list.mockRejectedValueOnce(new Error('Register unavailable'));
    renderPage();
    expect(await screen.findByText('Register unavailable')).toBeInTheDocument();
  });
});
