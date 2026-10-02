import '@testing-library/jest-dom/vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Application } from './App';

vi.mock('./auth/AuthProvider', () => ({
  useAuth: () => ({ user: { profile: { preferred_username: 'alice' } }, logout: vi.fn() }),
}));
afterEach(cleanup);

describe('authenticated application shell', () => {
  it('shows keyboard-operable global navigation and identifies the active route', () => {
    render(
      <QueryClientProvider
        client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
      >
        <MemoryRouter initialEntries={['/adls']}>
          <Application />
        </MemoryRouter>
      </QueryClientProvider>,
    );
    expect(screen.getByRole('navigation', { name: 'Main navigation' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Home' })).toHaveAttribute('href', '/');
    expect(screen.getByRole('link', { name: 'Decision Logs' })).toHaveAttribute('href', '/adls');
    expect(screen.getByRole('link', { name: 'Decision Logs' })).toHaveAttribute(
      'aria-current',
      'page',
    );
  });

  it('keeps the primary navigation operable on a narrow viewport', () => {
    Object.defineProperty(window, 'innerWidth', { configurable: true, value: 320 });
    render(
      <QueryClientProvider
        client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
      >
        <MemoryRouter initialEntries={['/']}>
          <Application />
        </MemoryRouter>
      </QueryClientProvider>,
    );
    expect(screen.getByRole('link', { name: 'Home' })).toBeVisible();
    expect(screen.getByRole('link', { name: 'Decision Logs' })).toBeVisible();
    expect(screen.getByRole('link', { name: 'Create ADL' })).toHaveAttribute('href', '/adls/new');
  });
});
